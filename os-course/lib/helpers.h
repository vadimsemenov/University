#ifndef OS_HELPERS
#define OS_HELPERS

#include <sys/types.h>
#include <sys/uio.h>
#include <unistd.h>

ssize_t read_(int fd, void *buf, size_t nbyte);
ssize_t write_(int fd, const void *buf, size_t nbyte);

#endif // OS_HELPERS
