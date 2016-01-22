#include <bits/stdc++.h>

int main() {
  int length;
  std::cin >> length;
  std::string num(length, '0');
  std::cout << num << std::endl;
  int qty;
  std::cin >> qty;
  int ptr = (qty == length ? length : 0);
  const char BUBEN = '0' + '1';
  while (qty < length) {
    num[ptr] = BUBEN - num[ptr];
    std::cout << num << std::endl;
    int _;
    std::cin >> _;
    assert(abs(_ - qty) == 1);
    if (_ > qty) {
      qty = _;
    } else {
      num[ptr] = BUBEN - num[ptr];
    }
    ++ptr;
  }
  assert(qty == length);
  return 0;
}
