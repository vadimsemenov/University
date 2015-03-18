#ifndef OS_HELPERS
#define OS_HELPERS

#include <unistd.h>
#include <sys/types.h>

ssize_t read_(int fd, void *buf, size_t count);
ssize_t write_(int fd, const void *buf, size_t count);

ssize_t read_until(int fd, void *buf, size_t count, const char delimiter);

#endif // OS_HELPERS
