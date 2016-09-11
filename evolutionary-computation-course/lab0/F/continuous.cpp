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

struct Node {
  int left, right;
  double la, lb, ra, rb;

  Node(int l, int r):
    left(l), right(r), la(0.0), lb(0.0), ra(0.0), rb(0.0) {
  }
};

int main() {
  redirect("continuous.in", "continuous.out");
  int states_qty, counter;
  std::cin >> states_qty >> counter;
  std::vector<Node *> automaton(states_qty);
  for (int i = 0; i < states_qty; ++i) {
    int l, r;
    std::cin >> l >> r;
    automaton[i] = new Node(l - 1, r - 1);
  }
  for (int it = 0; it < counter; ++it) {
    int length;
    std::cin >> length;
    // std::vector<int> input(length);
    std::vector<double> output(length);
    // for (int i = 0; i < length; ++i) {
      // std::cin >> input[i];
    // }
    std::string input; std::cin >> input;
    for (int i = 0; i < length; ++i) {
      std::cin >> output[i];
    }
    for (int i = length - 1; i > 0; --i) {
      output[i] -= output[i - 1];
    }
    assert(length == int(input.size()) && length == int(output.size()));
    int current = 0;
    double mul = 1.0 / length;
    for (int i = 0; i < length; ++i) {
      if (input[i] == '0') {
        automaton[current]->la -= mul;
        automaton[current]->lb += 2 * mul * output[i];
        current = automaton[current]->left;
      } else if (input[i] == '1') {
        automaton[current]->ra -= mul;
        automaton[current]->rb += 2 * mul * output[i];
        current = automaton[current]->right;
      } else {
        assert(false);
      }
    }
  }
  std::cout << std::fixed << std::setprecision(9);
  for (int i = 0; i < states_qty; ++i) {
    double l = std::abs(automaton[i]->la) < 1e-9 ? 0.0 : -automaton[i]->lb / 2 / automaton[i]->la;
    double r = std::abs(automaton[i]->ra) < 1e-9 ? 0.0 : -automaton[i]->rb / 2 / automaton[i]->ra;
    std::cout << l << ' ' << r << std::endl;
  }
}
