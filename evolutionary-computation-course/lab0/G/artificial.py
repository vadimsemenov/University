x = int(open("artificial.in", "r").read())
with open("artificial.out", "w") as f:
    if x == 1:
        f.write("""3 3 M M
3 1 R M
4 2 R M
3 4 L M
""")
    if x == 2:
        f.write("""1 1 L M
""")
    if x == 3:
        f.write("""3 3 R M
3 2 L M
1 2 M M
""")
    if x == 4:
        f.write("""4 1 M M
1 2 L M
1 4 R M
3 2 M M
""")
    if x == 5:
        f.write("""2 3 M M
3 2 M L
1 1 R M
""")
    if x == 6:
        f.write("""4 5 M M
4 1 L M
2 1 M M
5 1 R M
2 3 M M
""")
    if x == 7:
        f.write("""4 2 M M
3 1 M M
3 1 R M
1 2 L M
""")
    if x == 8:
        f.write("""2 1 M M
4 4 R M
5 5 R M
3 1 R M
3 1 M M
""")
    if x == 9:
        f.write("""6 5 R M
4 3 L M
1 5 M M
3 1 R M
4 2 M M
2 2 L M
""")
    if x == 10:
        f.write("""4 3 L M
6 5 R M
4 2 R M
2 2 M M
1 2 R M
1 1 L M
""")
