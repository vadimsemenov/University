#! /bin/bash

echo -ne "/bin/sh\n/blablabla\n/bin/cat\n" | ./filter tar cf /tmp/filter.tar
