CC = gcc
CFLAGS = -Wall -Wextra -std=c99

all: libhelpers.so libbufio.so

libhelpers.so: helpers.c
	$(CC) $(CFLAGS) -shared -o libhelpers.so -fPIC helpers.c 

libbufio.so: bufio.c
	$(CC) $(CFLAGS) -shared -o libbufio.so -fPIC bufio.c

clean:
	rm -f *.so

