#include <sys/types.h>

#define SIZEOF_BUF(capacity) (sizeof(struct buf_t) + sizeof(char) * (capacity)) 
#define DATA(buf) ((char *) (buf + 1))

struct buf_t {
  size_t capacity;
  size_t size;
};

struct buf_t *buf_new(size_t capacity);
void buf_free(struct buf_t *);

size_t buf_capacity(const struct buf_t *);
size_t buf_size(const struct buf_t *);

ssize_t buf_fill(int fd, struct buf_t *buf, size_t required);
ssize_t buf_flush(int fd, struct buf_t *buf, size_t required);

