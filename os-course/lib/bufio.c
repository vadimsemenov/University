#include "bufio.h"

#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#ifndef DEBUG
#define NDEBUG
#endif
#include <assert.h>

#include <stdio.h>

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

ssize_t buf_flush(int fd, struct buf_t *buf, size_t required) {
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

ssize_t buf_getline(int fd, struct buf_t *buf, char *dest) {
  assert(buf);
  size_t offset = 0;
  int found_EOL = 0;
  while (!found_EOL) {
    size_t i;
    for (i = 0; i < buf->size; ++i) {
      dest[offset + i] = *(DATA(buf) + i);
      if (dest[offset + i] == '\n') {
        found_EOL = 1;
        ++i;
        break;
      }
    }
    offset += i;
    buf->size -= i;
    memmove(DATA(buf), DATA(buf) + i, buf->size);
    ssize_t cnt = 0;
    if (!found_EOL && (cnt = buf_fill(fd, buf, 1)) < 0) {
      return -1;
    }
    if (cnt == 0) {
      break;
    }
  }
  return offset;
}
