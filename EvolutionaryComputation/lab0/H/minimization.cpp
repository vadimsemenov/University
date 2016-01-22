#include <bits/stdc++.h>

const int MAGIC = 40;
std::vector<double> result;

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
double solve(int dim) {
  const double mul = (3.0 - sqrt(5.0)) / 2.0;
  const int BOTVA = 20;
  double best = query();
  for (int i = 0; i < MAGIC; ++i) {
    double ll = (double) i / MAGIC;
    result[dim] = ll;
    double l_ = query();
    if (l_ < best) {
      best = l_;
    }

    double rr = (double) (i + 1) / MAGIC;
    result[dim] = rr;
    double r_ = query();
    if (r_ < best) {
      best = r_;
    }

    double mm = ll + mul * (rr - ll);
    result[dim] = mm;
    double m_ = query();
    if (m_ > l_ && m_ > r_) { // not convex
      continue;
    } else if (m_ < best) {
      best = m_;
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
        if (m_ < best) {
          best = m_;
        }
      } else {
        if (lhs) {
          ll = mm;
          l_ = m_;
        } else {
          rr = mm;
          r_ = m_;
        }
        if (_ < best) {
          best = _;
        }
      }
    }
    result[dim] = (ll + rr) / 2;
    double foo = query();
    if (foo < best) {
      best = foo;
    }
  }
  return best;
}

int main() {
  std::cout << std::fixed << std::setprecision(9);
  int dims; std::cin >> dims;
  result.resize(dims);
  double best = query();
  for (int d = 0; d < dims; ++d) {
    double foo = solve(d);
    if (best > foo) {
      best = foo;
    }
  }
  std::cout << "minimum " << best << std::endl;
  return 0;
}
