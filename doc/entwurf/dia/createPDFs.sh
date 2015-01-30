#!/bin/bash
FILES=*.svg
for f in $FILES
do
  # take action on each file. $f store current file name
  # cat $f
  inkscape $f -A ${f%???}pdf
done
