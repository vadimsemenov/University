CC = gcc
CFLAGS = -Wall -Wextra

all: libhelpers

libhelpers: helpers.c
	$(CC) $(CFLAGS) -shared -o libhelpers.so -fPIC helpers.c 

# helpers.o: helpers.c
#	$(CC) $(CFLAGS) -c -fPIC -o helpers.o helpers.c

clean:
	rm -f *.so

