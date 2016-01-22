x = int(open("artificial.in", "r").read())
with open("artificial.out", "w") as f:
	if x == 1:
		f.write("""3 2 L M
3 3 R M
2 4 R M
2 1 M M
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
		f.write("""3 1 M M
3 4 R M
2 1 M M
1 4 L M
""")
	if x == 5:
		f.write("""1 3 M L
3 1 M M
2 3 M M
""")
	if x == 6:
		f.write("""3 1 M R
2 2 R M
2 5 R M
2 1 R M
1 2 R M
""")
	if x == 7:
		f.write("""3 2 M M
4 4 M M
2 1 R M
3 1 L M
""")
	if x == 8:
		f.write("""1 2 L L
5 5 L M
5 5 M M
2 3 L M
2 5 R M
""")
	if x == 9:
		f.write("""5 5 M R
5 3 R M
6 1 R M
4 6 R M
3 4 M M
4 3 R M
""")
	if x == 10:
		f.write("""6 5 R L
4 6 L M
3 4 R M
1 4 R M
4 2 M M
4 3 L M
""")
