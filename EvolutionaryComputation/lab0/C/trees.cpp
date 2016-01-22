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
  int predicate_id;
  int left, right;
  int new_id;

  Node(int id):
    predicate_id(id), left(-1), right(-1) {
  }

  Node(int id, int l, int r):
    predicate_id(id), left(l), right(r) {
  }

  bool is_leaf() {
    return left == -1; // && right == -1
  }

  void print() {
    if (is_leaf()) {
      std::cout << "leaf " << predicate_id << std::endl;
    } else {
      std::cout << "choice " << predicate_id << " " << left + 1 << " " << right + 1 << std::endl;
    }
  }
};

void simplify(int id, int parent, std::vector<Node *> &tree, std::unordered_map<int, bool> &ancestors, int &new_vertices_qty) {
  if (tree[id]->is_leaf()) {
    tree[id]->new_id = ++new_vertices_qty;
    return;
  } else if (ancestors.count(tree[id]->predicate_id)) {
    int new_child = (ancestors[tree[id]->predicate_id] ? tree[id]->right : tree[id]->left);
    if (tree[parent]->left == id) {
      tree[parent]->left = new_child;
    } else if (tree[parent]->right == id) {
      tree[parent]->right = new_child;
    } else {
      std::cerr << "bad tree" << std::endl;
      assert(false);
    }
    simplify(new_child, parent, tree, ancestors, new_vertices_qty);
  } else {
    tree[id]->new_id = ++new_vertices_qty;
    ancestors[tree[id]->predicate_id] = false;
    simplify(tree[id]->left, id, tree, ancestors, new_vertices_qty);
    ancestors[tree[id]->predicate_id] = true;
    simplify(tree[id]->right, id, tree, ancestors, new_vertices_qty);
    ancestors.erase(tree[id]->predicate_id);
  }
}

void print(int id, std::vector<Node *> &tree) {
  if (tree[id]->is_leaf()) {
    std::cout << "leaf " << tree[id]->predicate_id << std::endl;
  } else {
    std::cout << "choice " << tree[id]->predicate_id << " " << tree[tree[id]->left]->new_id << " " << tree[tree[id]->right]->new_id << std::endl;
    print(tree[id]->left, tree);
    print(tree[id]->right, tree);
  }
}

int main() {
  redirect("trees.in", "trees.out");
  int vertices_qty;
  std::cin >> vertices_qty;
  std::vector<Node *> tree(vertices_qty);
  for (int i = 0; i < vertices_qty; ++i) {
    std::string type; std::cin >> type;
    if (type == "leaf") {
      int id; std::cin >> id;
      tree[i] = new Node(id);
    } else if (type == "choice") {
      int id, l, r; std::cin >> id >> l >> r;
      tree[i] = new Node(id, l - 1, r - 1);
    } else {
      std::cerr << "unknown type: '" << type << "'" << std::endl;
      assert(false);
    }
  }
  std::unordered_map<int, bool> ancestors;
  int new_vertices_qty = 0;
  simplify(0, 0, tree, ancestors, new_vertices_qty);
  std::cout << new_vertices_qty << std::endl;
  print(0, tree);
  return 0;
}
