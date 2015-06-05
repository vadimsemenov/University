#define _GNU_SOURCE

#include "helpers.h"

#include <unistd.h>
#include <stdlib.h>

#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <wait.h>
#include <signal.h>


/* Task #6 */
execargs_t new_execargs(size_t argc) {
//  execargs_t result = (execargs_t) malloc(sizeof(struct execargs) + (2 + argc) * sizeof(char *));
  execargs_t result = (execargs_t) malloc(sizeof(struct execargs));
  if (result == NULL) return result;
  result->argv = (char **) malloc((2 + argc) * sizeof(char *));
  result->argc = argc;
  result->argv[argc + 1] = NULL;
  return result;
}

void set_args(execargs_t program, char **args) {
  if (program == NULL) return;
  memmove(program->argv, args, (program->argc + 2));
}

void set_file(execargs_t program, char *file) {
  if (program == NULL) return;
  program->file = file;
  program->argv[0] = file;
}

execargs_t new_bulk_execargs(char *file, size_t argc, char **argv) {
  execargs_t result = new_execargs(argc);
  set_file(result, file);
  result->argc = argc;
  result->argv = argv;
  return result;
}

void print_execargs(FILE *file, execargs_t execarg) {
  fprintf(file, "%s (%d): ", execarg->file, (int) execarg->argc);
  size_t i;
  for (i = 0; i <= execarg->argc; ++i) {
    fprintf(file, "%s ", execarg->argv[i]);
  }
  fprintf(file, "\n");
}

int exec(execargs_t args) {
  return execvp(args->file, args->argv);
}

int runpiped(execargs_t *programs, size_t n) {
  pid_t children[n];
  fprintf(stderr, "runpiped(..., %d)\n",(int) n);
  memset(children, 0, n * sizeof(pid_t));
  int input = dup(STDIN_FILENO);
  int pipefd[2];
  size_t i;
for (i = 0; i < n; ++i) print_execargs(stderr, programs[i]);
  for (i = 0; i < n; ++i) {
    if (pipe2(pipefd, O_CLOEXEC) < 0) {
      goto CLOSE_ALL;
    }
    children[i] = fork();
    if (children[i] == 0) { /* Child */
      close(pipefd[0]);
      fprintf(stderr, "Hello, i'm child (%d)\n", getpid());
      if (i > 0 && dup2(input, STDIN_FILENO) < 0) {
        _exit(errno);
      }
      if (i + 1 < n && dup2(pipefd[1], STDOUT_FILENO) < 0) {
        _exit(errno);
      }
      _exit(exec(programs[i]));
    } else if (children[i] < 0) { /* Error */
      perror("Cannot fork :(");
      close(pipefd[0]);
      close(pipefd[1]); 
      goto CLOSE_ALL;
    } else { /* Parent */
      fprintf(stderr, "child (%d)\n", children[i]);
      close(pipefd[1]);
      /* Redirect read end of pipe to input */
      if (dup2(pipefd[0], input) < 0) {
        fprintf(stderr, "uuups %d -> %d", pipefd[0], input);
        goto CLOSE_ALL;
      }
    }
  }
CLOSE_ALL:
  if (input != -1 && input != STDOUT_FILENO && input != STDIN_FILENO) {
    close(input);
  }
fprintf(stderr, "chlidren termination, stdin(%d), stdout(%d)\n", STDIN_FILENO, STDOUT_FILENO); 
  /* Wait for all children termination */
  for (i = 0; i < n; ++i) {
    if (children[i] <= 0) continue;
    int status;
    int childid;
    if ((childid = waitpid(children[i], &status, 0)) < 0) {
      fprintf(stderr, "%d\n", childid);
      fprintf(stderr, "Child (%d) failed\n", children[i]);
      perror("");
      kill(children[i], SIGKILL); 
    } else {
      if (!WIFEXITED(status)/* || WEXITSTATUS(status) != 0*/) {
        fprintf(stderr, "%d - %d\n", WIFEXITED(status), WEXITSTATUS(status));
        fprintf(stderr, "Child (%d) failed\n", childid);
        kill(children[i], SIGKILL); 
      }
    }
  }
  return 0;
}

/* Task #3 */
int spawn(const char *file, char *const argv[]) {
  pid_t child_id = fork();
  if (child_id == -1) {
    goto ERROR;
  } else if (child_id == 0) { /* Child thread */
    execvp(file, argv);
    goto ERROR;
  } else { /* Parent thread */
    int status;
    wait(&status); /* There is only one child */
    if (WIFEXITED(status)) {
      return WEXITSTATUS(status);
    }
    goto ERROR;
  }

ERROR:
  return -1;
}

/* Task #1 */
ssize_t read_(int fd, void *buf, size_t count) {
  size_t allready_readed = 0;
  while (allready_readed < count) {
    ssize_t readed = read(fd, buf + allready_readed, count - allready_readed);
    if (readed == -1) {
      return readed;
    }
    if (readed == 0) {
      break;
    }
    allready_readed += readed;
  }
  return allready_readed;
}

ssize_t write_(int fd, const void *buf, size_t count) {
  size_t allready_writed = 0;
  while (allready_writed < count) {
    ssize_t writed = write(fd, buf + allready_writed, count - allready_writed);
    if (writed == -1) {
      return writed;
    }
    allready_writed += writed;
  }
  return allready_writed;
}

/* Task #2 */
ssize_t read_until(int fd, void *buf, size_t count, const char delimiter) {
  size_t allready_readed = 0;
  int contains_delimiter = 0;
  while (allready_readed < count && contains_delimiter == 0) {
    ssize_t readed = read(fd, buf + allready_readed, count - allready_readed);
    if (readed == -1) {
      return readed;
    }
    if (readed == 0) {
      break;
    }
    for (size_t i = allready_readed; i < allready_readed + readed; ++i) {
      if (*((char *) buf + i) == delimiter) {
        contains_delimiter = 1;
        break;
      }
    }
    allready_readed += readed;
  }
  return allready_readed;
}
