#include "helpers.h"

#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>


/* Task #6 */
execargs_t *new_execargs(size_t argc) {
  execargs_t *result = (execargs_t *) malloc(sizeof(execargs_t) + (2 + argc) * sizeof(char *));
  if (result == NULL) return result;
  result->argc = argc;
  result->argv[argc + 1] = NULL;
  return result;
}

void set_args(execargs_t *program, char **args) {
  if (program == NULL) return;
  memmove(program->argv + sizeof(char *), args, program->argc);
}

void set_file(execargs_t *program, char *file) {
  if (program == NULL) return;
  program->file = file;
  program->argv[0] = file;
}

execargs_t *new_bulk_execargs(char *file, size_t argc, char **argv) {
  execargs_t *result = new_execargs(argc);
  set_file(result, file);
  set_args(result, argv);
  return result;
}

int exec(execargs_t *args) {
  return execvp(args->file, args->argv);
}

int runpiped(execargs_t **programs, size_t n) {
  int *pipefd = (int *) malloc(2 * n * sizeof(int));
  pipefd[0] = STDOUT_FILENO;
  pipefd[2 * n - 1] = STDIN_FILENO;
  size_t i;
  for (i = 0; i + 1 < n; ++i) {
    /* Bind ith end of the pipe with (i+1)th */
    if (pipe2(pipefd + 1 + 2 * i, O_CLOEXEC) < 0) {
      /* Fail, close all opened fd */
      size_t j;
      for (j = 0; j < 2 * i; ++j) {
        close(pipefd[j]);
      }
      free(pipefd);
      return -1;
    }
  }
  /* So ith process write end of the pipe is pipefd[2 * (n - 1 - i)]
     and read end -- pipefd[2 * (n - 1 - i) + 1] */
  
  int *childid = (int *) malloc(n * sizeof(int));
  for (i = 0; i < n; ++i) {
    childid[i] = fork();
    if (childid[i] == 0) {
      if (dup2(pipefd[2 * (n - 1 - i) + 1], STDIN_FILENO) < 0) {
        _exit(errno);
      }
      if (dup2(pipefd[2 * (n - 1 - i)], STDOUT_FILENO) < 0) {
        _exit(errno);
      }
      /* Why we don't need to close all unused fds?.. */
      return exec(programs[i]);
    } else if (childid[i] < 0) {
      size_t j;
      for (j = 1; j + 1 < 2 * n; ++j) {
        close(pipefd[j]);
      }
      for (j = 0; j < i; ++j) {
        kill(childid[j], SIGKILL); 
        waitpid(childid[j], NULL, 0); /* Avoid zombies! Oo */
      }
      free(childid);
      free(pipefd);
      return -1;
    }
  }

  /* Wait for all children termination */
  wait(NULL);

  free(childid);
  free(pipefd);
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
