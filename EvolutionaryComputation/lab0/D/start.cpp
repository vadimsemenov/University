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

int main() {
  redirect("start.in", "start.out");
  int length, states_qty;
  std::cin >> length >> states_qty;
  auto automaton = std::vector<std::pair<int, int>>(states_qty);
  auto states = std::vector<std::vector<int>>('z' - 'a' + 1);
  for (int i = 0; i < states_qty; ++i) {
    int l, r;
    char ch;
    std::cin >> l >> r >> ch;
    automaton[i] = std::make_pair(l - 1, r - 1);
    states[ch - 'a'].push_back(i);
  }
  std::string output;
  std::cin >> output;
  assert(length == int(output.length()));
  auto dp = std::vector<std::vector<bool>>(2, std::vector<bool>(states_qty, false));
  int cur = 0;
  std::fill(dp[cur].begin(), dp[cur].end(), true);
  for (int i = length - 1; i >= 0; --i) {
    cur ^= 1;
    std::fill(dp[cur].begin(), dp[cur].end(), false);
    for (int v : states[output[i] - 'a']) {
      dp[cur][v] = dp[cur ^ 1][automaton[v].first] || dp[cur ^ 1][automaton[v].second];
    }
  }
  int qty = 0;
  for (int i = 0; i < states_qty; ++i) {
    if (dp[cur][automaton[i].first] || dp[cur][automaton[i].second]) {
      ++qty;
    }
  }
  std::cout << qty;
  for (int i = 0; i < states_qty; ++i) {
    if (dp[cur][automaton[i].first] || dp[cur][automaton[i].second]) {
      std::cout << " " << i + 1;
    }
  }
  std::cout << std::endl;
}
