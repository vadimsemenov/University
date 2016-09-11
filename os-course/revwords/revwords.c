#include "helpers.h"
#include <string.h>
#include <stdio.h>

#define MAX_SIZE 4097

void reverse(char *from, char *to) {
  for (size_t i = 0; i < (size_t) (to - from + 1) / 2; ++i) {
    char tmp = *(from + i);
    *(from + i) = *(to - i);
    *(to - i) = tmp;
  }
}

ssize_t print_reversed(char *buf, size_t count) {
  char *to = buf + count - 1;
  if (*to == ' ') {
    --to;
  }
  reverse(buf, to);
  return write_(STDOUT_FILENO, buf, count);
}

int main(void) {
  static char buf[MAX_SIZE];
  size_t proceeded = 0;
  size_t allready_readed = 0;
  ssize_t readed;
  while ((readed = read_until(STDIN_FILENO, 
            buf + allready_readed, MAX_SIZE - allready_readed, ' ')) != 0) {
    if (readed == -1) {
      goto ERROR;
    }
    for (size_t i = allready_readed; i < allready_readed + readed; ++i) {
      if (buf[i] == ' ') { 
        if (print_reversed(buf + proceeded, i - proceeded + 1) == -1) {
          goto ERROR;
        }
        proceeded = i + 1;
      }
    }
    allready_readed += readed;
    if (allready_readed == MAX_SIZE) {
      memcpy(buf, buf + proceeded, allready_readed - proceeded);
      allready_readed -= proceeded;
      proceeded = 0;
    }
  }
  if (print_reversed(buf + proceeded, allready_readed - proceeded) == -1) {
    goto ERROR;
  }
  return 0;

ERROR:
  fprintf(stderr, "Error in I/O");
  return 1;
}
