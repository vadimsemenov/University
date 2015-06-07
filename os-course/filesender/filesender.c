#define DEBUG
#define _POSIX_SOURCE

//#include "../lib/helpers.h"
//#include "../lib/bufio.h"
//#include <helpers.h>
#include <bufio.h>

#include <stdio.h>
#include <string.h>

#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/wait.h>

#include <sys/socket.h>
#include <netdb.h>

#ifdef DEBUG

#define assume(val, exit_status)  { \
  if (!(val)) { \
    perror(#val); \
    return exit_status; \
  } \
}

#define log(...)  do { fprintf(stderr, __VA_ARGS__); } while (0);

#else
#define assume(val, exit_status) do { if (!(val)) {} } while (0);
#define log(...) {}
#endif

/**
 * Sends file to dstfd using bufio
 * returns EXIT_SUCCESS iff sending ends successfully
 * EXIT_FAILURE otherwise
 */
int send_file(const int dstfd, const char *filename) {
  log("sending file to %d\n", dstfd);
  assume(dstfd >= 0 && filename != NULL, EXIT_FAILURE);
  static const int BUF_SIZE = 4096;
  struct buf_t *buffer = buf_new(BUF_SIZE);
  const int srcfd = open(filename, O_RDONLY);
  assume(srcfd >= 0, EXIT_FAILURE);
  int exit_status = EXIT_SUCCESS;
  while (1) {
    assume(buf_size(buffer) + 1 < buf_capacity(buffer), EXIT_FAILURE);
    ssize_t cnt = buf_fill(srcfd, buffer, buf_size(buffer) + 1);
    if (cnt < 0) {
      perror("Input fail");
      exit_status = EXIT_FAILURE;
      break;
    }
    if (cnt == 0) break;
    assume(buf_size(buffer) > 0, EXIT_FAILURE);
    if (buf_flush(dstfd, buffer, 1) < 0) {
      perror("Output fail");
      exit_status = EXIT_FAILURE;
      break;
    }
  }
  if (exit_status == EXIT_SUCCESS && buf_size(buffer) > 0) {
    assume(buf_flush(dstfd, buffer, buf_size(buffer)) >= 0, EXIT_FAILURE);
  }

  log("ended sending to %d\n", dstfd);
  close(srcfd);
  buf_free(buffer);
  return exit_status;
}

/**
 * Creates listening (blocked) socket on host:port
 * returns socket fd iff succeed, -1 otherwise
 */
int create_listening_socket(const char *host, const char *port) {
  log("creating listening socket on %s:%s\n", host, port);
  struct addrinfo hints;
  memset(&hints, 0, sizeof(hints));
  hints.ai_family = AF_INET; // IPv4
  hints.ai_socktype = SOCK_STREAM; // TCP socket
  struct addrinfo *res = NULL;
  assume(getaddrinfo(host, port, &hints, &res) == 0, -1);
  struct addrinfo *rp;
  int sockfd;
  for (rp = res; rp != NULL; rp = rp->ai_next) {
    sockfd = socket(AF_INET, rp->ai_socktype, rp->ai_protocol);
    if (sockfd < 0) continue;
    if (bind(sockfd, rp->ai_addr, rp->ai_addrlen) == 0) break;
    close(sockfd);
  }
  assume(rp != NULL, -1);
  freeaddrinfo(res);
  assume(listen(sockfd, 1) == 0, -1); 
  return sockfd;
}

int main(int argc, char **argv) {
  if (argc != 3) {
    fprintf(stdout, "Usage: %s <port> <filename>\n", argv[0]);
    return EXIT_FAILURE;
  }
  const char *port = argv[1];
  const char *filename = argv[2];
  int acceptfd = create_listening_socket("localhost", port);
  assume(acceptfd >= 0, EXIT_FAILURE);
  while (1) {
    int fd = accept(acceptfd, NULL, NULL);
    if (fd < 0) {
      perror("Accept fail");
      continue;
    }
    pid_t child = fork();
    if (child < 0) { // Error
      perror("Couldn't fork");
      return EXIT_FAILURE;
    } else if (child == 0) { // Child
      _exit(send_file(fd, filename));
    } else { // Parent
      close(fd);
    }
  }
  // wait for children
  while (1) {
    int status;
    pid_t done = wait(&status);
    if (done < 0) {
      if (errno == ECHILD) break; // no more children
    } else {
      if (!WIFEXITED(status) || WEXITSTATUS(status) != 0) {
        fprintf(stderr, "Child (%d) failed\n", (int) done);
      }
    }
  }
  return EXIT_SUCCESS;
}
