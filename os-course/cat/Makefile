CC = gcc
CFLAGS = -Wall -Wextra
LIB_DIR = ../lib

all: cat

cat: libhelpers
	$(CC) $(CFLAGS) cat.c -o cat -I$(LIB_DIR) -L$(LIB_DIR) -lhelpers -Wl,-rpath,$(LIB_DIR) 

libhelpers:
	$(MAKE) -C $(LIB_DIR)

test:
	./test.sh

clean:
	$(MAKE) clean -C $(LIB_DIR)	
	rm -f ./cat

