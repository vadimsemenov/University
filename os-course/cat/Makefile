CC = gcc
CFLAGS = -Wall -Wextra
LIB_DIR = ../lib

cat: libhelpers
	$(CC) $(CFLAGS) cat.c -o cat -I$(LIB_DIR) -lheplers -L$(LIB_DIR) -Wl,-rpath,$(LIB_DIR) 

libhelpers:
	$(MAKE) -C $(LIB_DIR)

clean:
	$(MAKE) clean -C $(LIB_DIR)	
	rm -f ./cat

