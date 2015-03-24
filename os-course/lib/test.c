#include <helpers.h>
#include <stdio.h>

int main() {
  char *args[] = {"tar", "-cf", "./test.tar", "helpers.h"};
  int res = spawn("tar", args);
  printf("spawn() returned %d\n", res);
  return 0;
}
