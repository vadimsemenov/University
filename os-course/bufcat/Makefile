CC = gcc
CFLAGS = -Wall -Wextra -std=c99
LIB_DIR = ../lib

all: bufcat

bufcat: libhelpers
	$(CC) $(CFLAGS) bufcat.c -o bufcat -I$(LIB_DIR) -L$(LIB_DIR) -lhelpers -Wl,-rpath,$(LIB_DIR) 

libhelpers:
	$(MAKE) -C $(LIB_DIR)

test:
	./test.sh

clean:
	$(MAKE) clean -C $(LIB_DIR)	
	rm -f ./bufcat

