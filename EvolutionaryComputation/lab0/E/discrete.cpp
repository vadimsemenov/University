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
  std::vector<double> left_target, right_target;

  Node(int l, int r):
    left(l), right(r), left_target(26), right_target(26) {
  }
};

int main() {
  redirect("discrete.in", "discrete.out");
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
    std::string input, output;
    std::cin >> length >> input >> output;
    assert(length == int(input.length()) && length == int(output.length()));
    int current = 0;
    double ad = 1.0 / length;
    for (int i = 0; i < length; ++i) {
      int arc = output[i] - 'a';
      if (input[i] == '0') {
        automaton[current]->left_target[arc] += ad;
        current = automaton[current]->left;
      } else if (input[i] == '1') {
        automaton[current]->right_target[arc] += ad;
        current = automaton[current]->right;
      } else {
        assert(false);
      }
    }
  }
  for (int i = 0; i < states_qty; ++i) {
    int best_left = 0;
    int best_right = 0;
    for (int j = 1; j < 26; ++j) {
      if (automaton[i]->left_target[j] > automaton[i]->left_target[best_left]) {
        best_left = j;
      }
      if (automaton[i]->right_target[j] > automaton[i]->right_target[best_right]) {
        best_right = j;
      }
    }
    std::cout << char(best_left + 'a') << ' ' << char(best_right + 'a') << std::endl;
  }
}
