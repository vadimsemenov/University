//#include "../lib/helpers.h"
//#include "../lib/bufio.h"
#include <helpers.h>
#include <bufio.h>

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>

#define PREFIX "$ "
#define WHITESPACE ' '
#define PIPE_SEPARATOR '|'

#define BUF_SIZE 4096

void interact() {
  if (write_(STDOUT_FILENO, PREFIX, 3) < 0) {
    perror("Error in O");
    _exit(EXIT_FAILURE);
  }
}

int main() {
  static char line[BUF_SIZE];
  struct buf_t *buffer = buf_new(BUF_SIZE);
  while (1) {
    fprintf(stderr, "stdin: %d\n", STDIN_FILENO);
    interact();
    ssize_t _bytes = buf_getline(STDIN_FILENO, buffer, line);
fprintf(stderr, "bytes = %d\n", (int) _bytes);
    if (_bytes == 0) {
      break;
    }
    if (_bytes < 0) {
      perror("Error in IO");
      _exit(EXIT_FAILURE);
    }
    size_t bytes = (size_t) (_bytes - 1);
    int execute = 0;
    size_t programsQty = 1;
    size_t i;
    for (i = 0; i < bytes; ++i) {
      programsQty += (line[i] == PIPE_SEPARATOR);
      execute |= (line[i] != WHITESPACE);
    }
    if (!execute) {
      continue;
    }
fprintf(stderr, "programsQty = %d\n", (int) programsQty);
    execargs_t *programs = (execargs_t *) malloc(programsQty * sizeof(execargs_t));
    programsQty = 0;
    for (i = 0; i < bytes; ++i) {
      size_t j = i;
      size_t lexems = 0;
      while (1) {
        while (j < bytes && line[j] == WHITESPACE && line[j] != PIPE_SEPARATOR) {
          ++j;
        }
        if (j >= bytes || line[j] == PIPE_SEPARATOR) {
          break;
        }
        ++lexems;
        size_t k = j + 1;
        while (k < bytes && line[k] != PIPE_SEPARATOR && line[k] != WHITESPACE) {
          ++k;
        }
        j = k + 1;
      }
fprintf(stderr, "program %d, lexems = %d\n", (int) programsQty, (int) lexems);
      char **args = (char **) malloc((lexems + 1) * sizeof(char *));
      j = i;
      lexems = 0;
      while (1) {
        while (j < bytes && line[j] == WHITESPACE && line[j] != PIPE_SEPARATOR) {
          ++j;
        }
        if (j >= bytes || line[j] == PIPE_SEPARATOR) {
          break;
        }
        args[lexems++] = line + j;
        size_t k = j + 1;
        while (k < bytes && line[k] != PIPE_SEPARATOR && line[k] != WHITESPACE) {
          ++k;
        }
        line[k] = 0; /* End of argument */
        j = k + 1;
      }
      args[lexems] = NULL;
      programs[programsQty++] = new_bulk_execargs(args[0], lexems - 1, args);
      i = j;
    }
fprintf(stderr, "runpiped\n");
    if (runpiped(programs, programsQty) < 0) {
      perror("Fail :(");
    }
  }
  _exit(EXIT_SUCCESS);
}
