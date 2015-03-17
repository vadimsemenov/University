#ifndef OS_HELPERS
#define OS_HELPERS

#include <unistd.h>

ssize_t read_(int fd, void *buf, size_t count);
ssize_t write_(int fd, const void *buf, size_t count);

#endif // OS_HELPERS
