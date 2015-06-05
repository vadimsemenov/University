CC = gcc
CFLAGS = -g -Wall -Wextra -fsanitize=address -std=c99
LIB_DIR = ../lib

all: polling

polling: $(LIB_DIR)/libhelpers.so
	$(CC) $(CFLAGS) polling.c -o polling -I$(LIB_DIR) -L$(LIB_DIR) -lbufio -lhelpers -Wl,-rpath,$(LIB_DIR) 

$(LIB_DIR)/libhelpers.so:
	$(MAKE) -C $(LIB_DIR)

test:
	./test.sh

clean:
	$(MAKE) clean -C $(LIB_DIR)	
	rm -f ./polling

