#include <bits/stdc++.h>

void redirect(std::string in, std::string out) {
  { // input
    static std::filebuf input;
    input.open(in, std::ios::in);
    std::cin.rdbuf(&input);
    if (!std::cin) {
      std::cerr << "Failed to open '" << in << "'" << std::endl;
      exit(1);
    }
  }
  { // output
    static std::filebuf output;
    output.open(out, std::ios::out | std::ios::trunc);
    std::cout.rdbuf(&output);
    if (!std::cout) {
      std::cerr << "Failed to open '" << out << "'" << std::endl;
      exit(1);
    }
  }
}

int main() {
  redirect("mutation.in", "mutation.out");
  int length, counter;
  std::cin >> length >> counter;
  long long denominator = 1;
  for (int i = 0; i < length; ++i) {
    denominator *= length;
  }
  std::cout << std::fixed << std::setprecision(13);
  for (int i = 0; i < counter; ++i) {
    std::string from, to;
    std::cin >> from >> to;
    assert(from.length() == to.length() && int(from.length()) == length);
    long long numerator = 1;
    for (int j = 0; j < length; ++j) {
      if (from[j] == to[j]) {
        numerator *= length - 1;
      }
    }
    std::cout << (double) numerator / denominator << std::endl;
  }
  return 0;
}
