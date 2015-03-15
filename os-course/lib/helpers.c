#include "helpers.h"

ssize_t read_(int fd, void *buf, size_t nbyte) {
  size_t allready_readed = 0;
  while (allready_readed < nbyte) {
    ssize_t readed = read(fd, buf + allready_readed, nbyte - allready_readed);
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

ssize_t write_(int fd, const void *buf, size_t nbyte) {
  size_t allready_writed = 0;
  while (allready_writed < nbyte) {
    ssize_t writed = write(fd, buf + allready_writed, nbyte - allready_writed);
    if (writed == -1) {
      return writed;
    }
    allready_writed += writed;
  }
  return allready_writed;
}

