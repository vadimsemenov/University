CC = gcc
CFLAGS = -Wall -Wextra -std=c99
LIB_DIR = ../lib

all: revwords

revwords: $(LIB_DIR)/libhelpers.so 
	$(CC) $(CFLAGS) revwords.c -o revwords -I$(LIB_DIR) -L$(LIB_DIR) -lhelpers -Wl,-rpath,$(LIB_DIR)

$(LIB_DIR)/libhelpers.so:
	$(MAKE) -C $(LIB_DIR)

test:
	./test.sh

clean:
	$(MAKE) clean -C $(LIB_DIR)
	rm -f revwords

