#include "bufio.h"

#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#ifndef DEBUG
#define NDEBUG
#endif
#include <assert.h>

struct buf_t *buf_new(size_t capacity) {
  struct buf_t *buf = (struct buf_t *) malloc(SIZEOF_BUF(capacity));
  if (buf != NULL) {
    buf->capacity = capacity;
    buf->size = 0;
  }
  return buf;
}

void buf_free(struct buf_t *buf) {
  assert(buf);
  free(buf);
}

size_t buf_capacity(const struct buf_t *buf) {
  assert(buf);
  return buf->capacity;
}

size_t buf_size(const struct buf_t *buf) {
  assert(buf);
  return buf->size;
}

ssize_t buf_fill(int fd, struct buf_t *buf, size_t required) {
  assert(buf && required <= buf->capacity);
  ssize_t cnt; 
  while ((cnt = read(fd, DATA(buf) + buf->size, buf->capacity - buf->size)) != 0) {
    if (cnt == -1) {
      return cnt;
    }
    buf->size += cnt;  
    if (buf->size >= required || buf->size == buf->capacity) {
      break;
    }
  } 
  return buf->size;
}

ssize_t buf_flush(int fd, buf_t *buf, size_t required) {
  assert(buf);
  ssize_t cnt;
  size_t offset = 0;
  while ((cnt = write(fd, DATA(buf) + offset, buf->size - offset)) != 0) {
    if (cnt == -1) {
      return cnt; /* UB here */
    }
    offset += cnt;
    if (offset >= required || offset == buf->size) {
      break;
    }
  }
  memmove(DATA(buf), DATA(buf) + offset, buf->size - offset);
  buf->size -= offset;
  return offset;
}
