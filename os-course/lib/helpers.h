#ifndef OS_HELPERS
#define OS_HELPERS

#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

/* Task #1 */
ssize_t read_(int fd, void *buf, size_t count);
ssize_t write_(int fd, const void *buf, size_t count);

/* Task #2 */
ssize_t read_until(int fd, void *buf, size_t count, const char delimiter);

/* Task #3 */
int spawn(const char *file, char *const argv[]);

/* Task #5 */
struct execargs_t {
  char *file;

  size_t argc;
  char **argv; 
  /** Arguments in fromat: [file, arg0, arg1, ..., argn, NULL] */
};

int exec(execargs_t *args);
int runpiped(execargs_t **programs, size_t n);

#endif // OS_HELPERS
