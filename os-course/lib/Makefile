CC = gcc
CFLAGS = -Wall -Wextra

all: libhelpers

libhelpers: helpers.c
	$(CC) $(CFLAGS) -shared -o libhelpers.so -fPIC helpers.c 
#	$(CC) $(CFLAGS) -dynamiclib -o libhelpers.dylib -fPIC helpers.c

clean:
	rm -f *.so
#	rm -f *.dylib

