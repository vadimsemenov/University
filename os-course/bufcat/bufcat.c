#include <bufio.h>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define CAPACITY 1024

int main() {
  struct buf_t *buf = buf_new(CAPACITY);
  if (buf == NULL) {
    perror("Cannot create a buffer");
    return EXIT_FAILURE;
  }
  ssize_t cnt;
  while ((cnt = buf_fill(STDIN_FILENO, buf, buf_capacity(buf))) != 0) {
    if (buf_flush(STDOUT_FILENO, buf, buf_size(buf)) < 0) {
      goto IO_ERROR;
    }
    if (cnt < 0) { /* After flushing */
      goto IO_ERROR;
    }
  }
  buf_free(buf);
  return EXIT_SUCCESS;

IO_ERROR:
  buf_free(buf);
  perror("Error in IO");
  return EXIT_FAILURE;
}
