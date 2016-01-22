#include <bits/stdc++.h>

std::vector<double> answer;

inline void print_double(double x) {
  const char *foo = std::to_string(x).c_str();
  while (*foo != 0) {
    putchar(*foo);
    ++foo;
  }
}

char BUF[20];

inline double query() {
  for (double &x : answer) {
    // std::cout << x << ' ';
    print_double(x);
    putchar(' ');
  }
  puts("");
  fflush(stdout);
  scanf("%s", BUF);
  size_t ptr = 0;
  while (BUF[ptr] <= 32) ++ptr;
  if (BUF[ptr] == 'B' || BUF[ptr] == 'b') {
    exit(0);
  }
  return std::stod(BUF, &ptr);

  // std::cout << std::endl;
  // std::string retval;
  // std::cin >> retval;
  // #define getchar_unlocked getchar
  // char ch = getchar_unlocked();
  // while (ch <= 32) ch = getchar_unlocked();
  // if (ch == 'B') {
  //   exit(0);
  // } else {
  //   bool sign = ch == '-';
  //   if (sign) {
  //     ch = getchar_unlocked();
  //   }
  //   assert('0' <= ch && ch <= '9');
  //   unsigned long long num = ch - '0';
  //   int was = 0;
  //   int mult = 1;
  //   while ((ch = getchar_unlocked()) > 32) {
  //     if (ch == '.') {
  //       was = 1;
  //     } else if ('0' <= ch && ch <= '9') {
  //       mult *= was * 10;
  //       num = 10 * num + ch - '0';
  //     } else {
  //       assert(false);
  //     }
  //   }
  //   if (sign) mult = -mult;
  //   return (double) num / mult;
  // }
  /*
  if (retval == "Bingo") {
    exit(0);
  } else {
    double res;
    std::stringstream ss(retval);
    ss >> res;
    return res;
  }
  */
}

int main() {
  int dims; scanf("%d", &dims);
  answer.resize(dims);
  const double step = 0.01 / dims;
  const double from = -10.0;
  const double to = 10.0;
  const int REP = 5; //std::max(5 * dims, 5);
  const int ITERS = 2000 * dims; // from + step * ITERS == to;
  assert(dims * REP * ITERS <= 10000 * dims * dims);
  auto means = std::vector<std::vector<double>>(dims, std::vector<double>(ITERS, 0.0));
  for (int d = 0; d < dims; ++d) {
    double best_x = from;
    double best = 100500.0;
    for (int _ = 0; _ < REP; ++_) {
      for (int it = 0; it < ITERS; ++it) {
        const double x = to - step * it - step * 3.0 / 4;
        assert(from <= x && x <= to);
        answer[d] = x;
        double res = query();
        means[d][it] += res;
      }
    }
    for (int it = 0; it < ITERS; ++it) {
      const double x = to - step * it - step * 3.0 / 4;
      means[d][it] /= REP;
      if (means[d][it] < best) {
        best = means[d][it];
        best_x = x;
      }
    }
    answer[d] = best_x;
  }
  printf("Fail:(\n");
  // assert(false);
  return 0;
}
