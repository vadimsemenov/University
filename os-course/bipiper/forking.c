#define DEBUG

#ifndef _GNU_SOURCE
#define _GNU_SOURCE
#endif

//#include "../lib/bufio.h"
#include <bufio.h>

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>

#include <sys/socket.h>
#include <netdb.h>
#include <poll.h>


#ifdef DEBUG
#define log(...)  fprintf(stderr, __VA_ARGS__);

#define assume(what, exit_status) { \
  if (!(what)) { \
    perror(#what); \
    return exit_status; \
  } \
}
#else
#define log(...) {}
#define assume(what, exit_status)  do { if (!(what)) {} } while(0);
#endif

int make_server_socket(struct addrinfo *host) {
  struct addrinfo *rp;
  int sockfd = -1;
  for (rp = host; rp != NULL; rp = rp->ai_next) {
    sockfd = socket(host->ai_family, host->ai_socktype, 0);
    if (sockfd < 0) continue;
    if (bind(sockfd, host->ai_addr, host->ai_addrlen) == 0) break;
    close(sockfd);
  }
  assume(rp != NULL, -1);
  assume(listen(sockfd, 1) == 0, -1);
  return sockfd;
}

int do_pipe(int pipefd[2]) {
  log("Start piping from %d to %d\n", pipefd[0], pipefd[1]);
  static const int BUF_SIZE = 4096;
  struct buf_t *buffer = buf_new(BUF_SIZE);
  int exit_status = EXIT_SUCCESS;
  while (1) {
    assume(buf_size(buffer) < buf_capacity(buffer), EXIT_FAILURE);
    ssize_t read = buf_fill(pipefd[0], buffer, buf_size(buffer) + 1);
    if (read < 0) {
      fprintf(stderr, "Cannot read from fd %d: %s\n", pipefd[0], strerror(errno));
      exit_status = EXIT_FAILURE;
      break;
    } else if (read == 0) {
      break;
    }
    assume(buf_size(buffer) > 0, EXIT_FAILURE);
    if (buf_flush(pipefd[1], buffer, 1) < 0) {
      fprintf(stderr, "Cannot write to fd %d: %s\n", pipefd[1], strerror(errno));
      exit_status = EXIT_FAILURE;
      break;
    }
  }
  if (exit_status == EXIT_SUCCESS && buf_size(buffer) > 0) {
    if (buf_flush(pipefd[1], buffer, buf_size(buffer)) < 0) { // flush all buffer
      fprintf(stderr, "Cannot write to fd %d: %s\n", pipefd[1], strerror(errno));
      exit_status = EXIT_FAILURE;
    }
  }
  log("End piping from %d to %d\n", pipefd[0], pipefd[1]);
  return exit_status;
}

int main(int argc, char **argv) {
  if (argc != 3) {
    fprintf(stdout, "Usage: %s <port #1> <port #2>\n", argv[0]);
    return EXIT_FAILURE;
  }

  int fds[2];
  size_t i;
  for (i = 0; i < 2; ++i) {
    struct addrinfo *host;
    assume(getaddrinfo("localhost", argv[1 + i], NULL, &host) == 0, EXIT_FAILURE);
    fds[i] = make_server_socket(host);
    assume(fds[i] >= 0, EXIT_FAILURE);
    freeaddrinfo(host);
  }

  int pipe[2] = {-1, -1};
  while (1) {
    for (i = 0; i < 2; ++i) {
      if (pipe[i] == -1) {
        pipe[i] = accept(fds[i], NULL, NULL);
      }
    }
    if (pipe[0] >= 0 && pipe[1] >= 0) {
      for (i = 0; i < 2; ++i) {
        pid_t child = fork();
        if (child < 0) { // Error
          perror("Fork fail");
          _exit(EXIT_FAILURE);
        } else if (child == 0) { // Child
          _exit(do_pipe(pipe));
        } else { // Parent
          // Swap pipe ends
          pipe[0] ^= pipe[1]; 
          pipe[1] ^= pipe[0]; 
          pipe[0] ^= pipe[1];
        }
      }
      // In parent:
      for (i = 0; i < 2; ++i) {
        close(pipe[i]);
      }
      pipe[0] = pipe[1] = -1;
    }
  }
  
  return EXIT_SUCCESS;
}
