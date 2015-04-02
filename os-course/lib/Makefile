CC = gcc
CFLAGS = -Wall -Wextra -std=c99

all: libhelpers libbufio

libhelpers: helpers.c
	$(CC) $(CFLAGS) -shared -o libhelpers.so -fPIC helpers.c 

libbufio: bufio.c
	$(CC) $(CFLAGS) -shared -o libbufio.so -fPIC bufio.c

clean:
	rm -f *.so

