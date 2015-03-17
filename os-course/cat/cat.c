#include "helpers.h"
#include <stdio.h>

const size_t BUF_SIZE = 1024;

int main(void) {
  static char buf[1024];
  while (1) {
    ssize_t readed = read_(STDIN_FILENO, buf, BUF_SIZE);
    if (readed == -1) {
      fprintf(stderr, "an error while reading");
      return 1; 
    }
    if (readed == 0) {
      break;
    }
    ssize_t writed = write_(STDOUT_FILENO, buf, readed);
    if (writed == -1) {
      fprintf(stderr, "an error while writing");
      return 1;
    }
    if (readed != writed) {
      fprintf(stderr, "%zd != %zd", readed, writed);
      return 1;
    }
  }
  return 0;
}

