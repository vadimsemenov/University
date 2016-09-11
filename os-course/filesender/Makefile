CC = gcc
CFLAGS = -g -Wall -Wextra -fsanitize=address -std=c99
LIB_DIR = ../lib

all: filesender

filesender: $(LIB_DIR)/libhelpers.so
	$(CC) $(CFLAGS) filesender.c -o filesender -I$(LIB_DIR) -L$(LIB_DIR) -lbufio -Wl,-rpath,$(LIB_DIR) 

$(LIB_DIR)/libhelpers.so:
	$(MAKE) -C $(LIB_DIR)

test:
	./test.sh

clean:
	$(MAKE) clean -C $(LIB_DIR)	
	rm -f ./filesender

