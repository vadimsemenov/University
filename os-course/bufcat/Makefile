CC = gcc
CFLAGS = -Wall -Wextra -std=c99
LIB_DIR = ../lib

all: bufcat

bufcat: $(LIB_DIR)/libhelpers.so
	$(CC) $(CFLAGS) bufcat.c -o bufcat -I$(LIB_DIR) -L$(LIB_DIR) -lbufio -Wl,-rpath,$(LIB_DIR) 

$(LIB_DIR)/libhelpers.so:
	$(MAKE) -C $(LIB_DIR)

test:
	./test.sh

clean:
	$(MAKE) clean -C $(LIB_DIR)	
	rm -f ./bufcat

