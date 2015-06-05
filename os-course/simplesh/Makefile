CC = gcc
CFLAGS = -g -Wall -Wextra -std=c11
LIB_DIR = ../lib

all: simplesh

simplesh: $(LIB_DIR)/libhelpers.so
	$(CC) $(CFLAGS) simplesh.c -o simplesh -I$(LIB_DIR) -L$(LIB_DIR) -lbufio -lhelpers -Wl,-rpath,$(LIB_DIR) 

$(LIB_DIR)/libhelpers.so:
	$(MAKE) -C $(LIB_DIR)

test:
	./test.sh

clean:
	$(MAKE) clean -C $(LIB_DIR)	
	rm -f ./simplesh

