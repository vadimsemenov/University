#include <helpers.h>

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define BUF_SIZE 4096

/**
 * returns 0 if succeed, -1 if error ocсured, 1 otherwise
 */
ssize_t check(char **args, size_t position, char *arg, size_t length) {
  char foo = arg[length]; 
  arg[length] = 0;
  char *bar = args[position];
  args[position] = arg; 

  int status = spawn(args[0], args);
  ssize_t result = 1;
  if (status == 0) {
    arg[length] = '\n';
    result = write_(STDOUT_FILENO, arg, length + 1);
  } 

  args[position] = bar;
  arg[length] = foo;
  return result;
}

int main(int argc, char **argv) {
  if (argc < 2) {
    fprintf(stderr, "Usage: filter <command> [<aruments>]\n");
    return EXIT_FAILURE;
  }
  static char buf[BUF_SIZE + 1];
  char **args = (char **) malloc((argc + 1) * sizeof(char *));
  memmove(args, argv + 1, (argc - 1) * sizeof(char *));
  args[argc] = (char *) NULL;
  size_t proceed = 0;
  size_t allready_read = 0;
  ssize_t read = -1;
  while ((read = read_until(STDIN_FILENO, buf + allready_read, BUF_SIZE - allready_read, '\n')) != 0) {
    if (read == -1) {
      goto ERROR;
    } 
    size_t i;
    for (i = allready_read; i < allready_read + read; ++i) {
      if (buf[i] == '\n') {
        if (check(args, argc - 1, buf + proceed, i - proceed) == -1) {
          goto ERROR;
        }
        proceed = i + 1;
      }
    }
    allready_read += read;
    if (allready_read == BUF_SIZE) {
      memmove(buf, buf + proceed, allready_read - proceed);
      allready_read -= proceed;
      proceed = 0;
    }
  }
  if (check(args, argc - 1, buf + proceed, allready_read - proceed) == -1) {
    goto ERROR;
  }
  free(args);
  return EXIT_SUCCESS;

ERROR:
  free(args);
  perror("Error in IO\n");
  return EXIT_FAILURE;
}
