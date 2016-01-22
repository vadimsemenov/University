#include <bits/stdc++.h>

void redirect(std::string in, std::string out) {
  std::ios::sync_with_stdio(false);
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

bool one_point, two_point, uniform;
void print() {
  std::cout << (one_point ? "YES" : "NO") << std::endl;
  std::cout << (two_point ? "YES" : "NO") << std::endl;
  std::cout << (uniform ? "YES" : "NO") << std::endl;
  exit(0);
}

int main() {
  redirect("crossover.in", "crossover.out");
  int qty, length;
  std::cin >> qty >> length;
  std::vector<std::string> s(qty);
  for (int i = 0; i < qty; ++i) {
    std::cin >> s[i];
    assert(length == int(s[i].length()));
  }
  std::string target;
  std::cin >> target;
  assert(length == int(target.length()));
  std::vector<std::pair<int, int>> borders(qty, {0, length});
  for (int i = 0; i < qty; ++i) {
    int &l = borders[i].first;
    while (l < length && s[i][l] == target[l]) {
      ++l;
    }
    int &r = borders[i].second;
    while (r >= 0 && s[i][r] == target[r]) {
      --r;
    }
    if (l == length) {
      one_point = two_point = uniform = true;
      print();
    }
  }
  for (int i = 0; i < qty; ++i) {
    for (int j = 0; j < qty; ++j) {
      if (borders[i].first > borders[j].second) {
        one_point = two_point = uniform = true;
        print();
      }
    }
  }
  for (int i = 0; i < qty; ++i) {
    for (int j = 0; j < qty; ++j) {
      bool good = true;
      for (int k = borders[i].first; k <= borders[i].second; ++k) {
        if (target[k] != s[j][k]) {
          good = false;
          break;
        }
      }
      if (good) {
        two_point = uniform = true;
        print();
      }
    }
  }
  for (int i = 0; i < qty; ++i) {
    for (int j = 0; j < qty; ++j) {
      bool good = true;
      for (int k = 0; k < length; ++k) {
        good &= (target[k] == s[i][k] || target[k] == s[j][k]);
      }
      if (good) {
        uniform = true;
        print();
      }
    }
  }
  print();
}
