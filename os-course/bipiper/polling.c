#define _POSIX_C_SOURCE 2015
#define _GNU_SOURCE

#include <bufio.h>
#include <helpers.h>

#include <stdio.h>

#define assume(what, exit_status) { \
  if (!(what)) { \
    perror(#what); \
    return exit_status; \
  } \
}

#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>

#include <sys/socket.h>
#include <netdb.h>
#include <poll.h>

#define swap(type, fst, snd) { type tmp = fst; fst = snd; snd = tmp; }

#define MAX_CONNECTIONS 127
#define BUF_SIZE 2048
#define INITIAL_MASK (POLLIN | POLLOUT)

struct pollfd pfds[2 + 2 * MAX_CONNECTIONS];
struct buf_t *bufs[MAX_CONNECTIONS][2];
short mask[2 * MAX_CONNECTIONS];
size_t clients_qty;

int make_server_socket(struct addrinfo *host) {
  struct addrinfo *rp;
  int sockfd = -1;
  for (rp = host; rp != NULL; rp = rp->ai_next) {
    sockfd = socket(host->ai_family, SOCK_STREAM, 0);
    if (sockfd < 0) continue;
    if (bind(sockfd, host->ai_addr, host->ai_addrlen) == 0) break;
    close(sockfd);
  }
  assume(rp != NULL, -1);
  assume(listen(sockfd, 1) == 0, -1);
  return sockfd;
}

void close_pipe(size_t id) {
  size_t it;
  for (it = 0; it < 2; ++it) {
    mask[2 * id + it] = 0;
    close(pfds[2 + id * 2 + it].fd);
    buf_free(bufs[id][it]);
  }
  if (clients_qty >= 4) {
    size_t jd = ((clients_qty & ~1) - 2) >> 1;
    for (it = 0; it < 2; ++it) {
      swap(short, mask[2 * id + it], mask[2 * jd + it]);
      swap(struct pollfd, pfds[2 + 2 * id + it], pfds[2 + 2 * jd + it]);
      swap(struct buf_t *, bufs[id][it], bufs[jd][it]);
    }
  }
  clients_qty -= 2;
  if (clients_qty & 1) {
    size_t new_idx = clients_qty - 1;
    size_t old_idx = clients_qty + 1;
    swap(short, mask[new_idx], mask[old_idx]);
    swap(struct pollfd, pfds[new_idx], pfds[old_idx]);
    swap(struct buf_t *, bufs[new_idx >> 1][new_idx & 1], bufs[old_idx >> 1][old_idx & 1]);
  }
}

int main(int argc, char **argv) {
  if (argc != 3) {
    static char usage[] = "Usage: polling <port #1> <port #2>\n";
    write_(STDOUT_FILENO, usage, sizeof(usage));
    return EXIT_FAILURE;
  }

  memset(pfds, 0, sizeof(pfds));
  memset(bufs, 0, sizeof(bufs));
  memset(mask, 0, sizeof(mask));
  size_t i;
  for (i = 0; i < 2; ++i) {
    struct addrinfo *host;
    assume(getaddrinfo("localhost", argv[1 + i], NULL, &host) == 0, EXIT_FAILURE);
    pfds[i].fd = make_server_socket(host);
    assume(pfds[i].fd >= 0, EXIT_FAILURE);
    freeaddrinfo(host);
  }

  size_t listen_port = 0;
  pfds[listen_port].events = POLLIN;
  clients_qty = 0;
  while (1) {
    int cnt = poll(pfds, 2 + clients_qty, -1);
    if (cnt == -1) {
      if (errno == EINTR) continue;
      perror("poll");
      return EXIT_FAILURE;
    }
    // check accept
    short revent = pfds[listen_port].revents;
    if (revent & POLLIN) {
      int clientfd = accept(pfds[listen_port].fd, NULL, NULL);
      assume(clientfd >= 0, EXIT_FAILURE);
      bufs[clients_qty >> 1][clients_qty & 1] = buf_new(BUF_SIZE);
      mask[clients_qty] = INITIAL_MASK;
      pfds[2 + clients_qty].fd = clientfd;
      pfds[2 + clients_qty].events = mask[clients_qty] & (POLLIN | POLLRDHUP);
      ++clients_qty;
      pfds[2 + clients_qty].events = 0;
      pfds[listen_port].events = 0;
      listen_port ^= 1;
      if (clients_qty < 2 * MAX_CONNECTIONS) {
        pfds[listen_port].events = POLLIN;
      }
    } else {
      assume(!revent, EXIT_FAILURE);
    }

    for (i = 0; i < (clients_qty); ++i) {
      revent = pfds[2 + i].revents;
      struct buf_t *write_buffer = bufs[i >> 1][i & 1];
      struct buf_t *read_buffer = bufs[i >> 1][(i & 1) ^ 1];
      if (revent & POLLIN) {
        assume(buf_size(write_buffer) < buf_capacity(write_buffer), EXIT_FAILURE);
        buf_fill(pfds[2 + i].fd, write_buffer, buf_size(write_buffer) + 1);
        if (buf_size(write_buffer) == buf_capacity(write_buffer)) {
          pfds[2 + i].events &= (mask[i] & ~POLLIN); // cannot read more from this port
        }
        if (buf_size(write_buffer) > 0) {
          pfds[(2 + i) ^ 1].events |= (mask[i ^ 1] & POLLOUT); // can write to adjanced port
        }
      } 
      if (revent & POLLOUT) {
        assume(buf_size(read_buffer) > 0, EXIT_FAILURE);
        buf_flush(pfds[2 + i].fd, read_buffer, 1);
        if (buf_size(read_buffer) == 0) {
          pfds[2 + i].events &= (mask[i] & ~POLLOUT); // cannot write more to this port
        }
        if (buf_size(read_buffer) < buf_capacity(read_buffer)) {
          pfds[(2 + i) ^ 1].events |= (mask[i ^ 1] & POLLIN); // can read from adjanced port
        }
      }
      if (revent & POLLRDHUP) {
fprintf(stderr, "POLLRDHUP %d\n", i);
        assume(shutdown(pfds[(2 + i) ^ 1].fd, SHUT_WR) == 0, EXIT_FAILURE);
        mask[i ^ 1] &= ~POLLOUT;
        pfds[(2 + i) ^ 1].events &= mask[i ^ 1]; // cannot write more to adjanced port
        mask[i] &= ~POLLIN;
        pfds[2 + i].events &= mask[i]; // cannot read more from this port
      } 
      if (revent & POLLHUP) {
fprintf(stderr, "POLLHUP %d\n", i);
        assume(shutdown(pfds[(2 + i) ^ 1].fd, SHUT_RD) == 0, EXIT_FAILURE);
        mask[i ^ 1] &= ~POLLIN;
        pfds[(2 + i) ^ 1].events &= mask[i ^ 1];
        mask[i] &= ~POLLOUT;
        pfds[2 + i].events &= mask[i];
      } 
      if ((revent & POLLERR) || (mask[i] == 0 && mask[i ^ 1] == 0)) {
        close_pipe(i >> 1);
        i |= 1; // after swap there is new fd 
        i -= 2; // on ith position to check
      }
    }
  }
  
  return EXIT_SUCCESS;
}
