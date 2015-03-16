CC = gcc
CFLAGS = -Wall -Wextra

cat: libhelpers
	$(CC) $(CFLAGS) -I../lib -lhelpers -L../lib -Wl,-rpath,../lib -o cat cat.c

libhelpers:
	$(MAKE) -C ../lib

clean:
	$(MAKE) clean -C ../lib
	rm -f ./cat

