#! /bin/bash

./cat < cat.c > cat2.c && diff cat.c cat2.c && echo OK
rm -f cat2.c

