#include <bits/stdc++.h>

const int MAGIC = 27;
const int BOTVA = 999 / MAGIC - 4;
std::vector<double> result;
double best;

double query() {
  for (double &x : result) {
    std::cout << x << ' ';
  }
  std::cout << std::endl;
  double retval;
  std::cin >> retval;
  return retval;
}

/*
|..|.|..|
  x   x
x / (1 - x) = (1 - x) / 1
0 = 1 + x^2 - 3x
x = (3 +/- sqrt(5)) / 2
1 - 2x vs x
1      vs 3x
2      vs 9 - 3 * sqrt(5)
       <
*/
void solve(int dim) {
  const double mul = (3.0 - sqrt(5.0)) / 2.0;
  double best_ = 0;
  for (int i = 0; i < MAGIC; ++i) {
    double ll = (double) i / MAGIC;
    result[dim] = ll;
    double l_ = query();
    if (l_ < best) {
      best = l_;
      best_ = ll;
    }

    double rr = (double) (i + 1) / MAGIC;
    result[dim] = rr;
    double r_ = query();
    if (r_ < best) {
      best = r_;
      best_ = rr;
    }

    double mm = ll + mul * (rr - ll);
    result[dim] = mm;
    double m_ = query();
    if (m_ < best) {
      best = m_;
      best_ = mm;
    }
    bool lhs = true;

    for (int it = 0; it < BOTVA; ++it) {
      double mmm;
      if (lhs) {
        mmm = rr - mul * (rr - ll);
      } else {
        mmm = ll + mul * (rr - ll);
      }
      result[dim] = mmm;
      double _ = query();
      if (_ > m_) {
        if (lhs) {
          rr = mmm;
          r_ = _;
        } else {
          ll = mmm;
          l_ = _;
        }
        lhs = !lhs;
      } else {
        if (lhs) {
          ll = mm;
          l_ = m_;
        } else {
          rr = mm;
          r_ = m_;
        }
        mm = mmm;
        m_ = _;
      }
      if (m_ < best) {
        best = m_;
        best_ = mm;
      }
    }
    result[dim] = (ll + rr) / 2;
    double foo = query();
    if (foo < best) {
      best = foo;
      best_ = result[dim];
    }
  }
  result[dim] = best_;
}

int main() {
  std::cout << std::fixed << std::setprecision(9);
  int dims; std::cin >> dims;
  result.resize(dims);
  best = query();
  for (int d = 0; d < dims; ++d) {
    solve(d);
  }
  std::cout << "minimum " << best << std::endl;
  return 0;
}
