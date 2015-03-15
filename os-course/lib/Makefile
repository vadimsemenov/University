CC = gcc
CFLAGS = -Wall -Wextra

all: helpers.so

helpers.so: helpers.c
	$(CC) $(CFLAGS) -shared -o helpers.so -fPIC helpers.c 

# helpers.o: helpers.c
#	$(CC) $(CFLAGS) -c -fPIC -o helpers.o helpers.c

clean:
	rm *.so

