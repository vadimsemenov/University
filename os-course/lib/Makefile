CC = gcc
CFLAGS = -Wall -Wextra -std=c99

all: libhelpers

libhelpers: helpers.c
	$(CC) $(CFLAGS) -shared -o libhelpers.so -fPIC helpers.c 

clean:
	rm -f *.so

