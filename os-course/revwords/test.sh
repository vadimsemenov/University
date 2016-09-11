#! /bin/bash

(echo -ne "abc def\ngh" ; sleep 3; echo -ne "aa qwer") | ./revwords > output && echo -ne "cba aahg\nfed rewq" > expected && diff expected output && echo OK

rm -f expected output

