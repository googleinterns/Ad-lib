#!/bin/bash

set -e
FILES=`find backend/ -name "*.java" -type f`
echo "Found the following Java files:"
for file in $FILES; do
  echo $file
done
echo ""
EXIT=0
for file in $FILES; do
  echo -e "Running google-java-format on:\n $file\n"
  if ! java -jar ./google-java-format-1.8-all-deps.jar $file | diff $file -; then
    EXIT=1
  fi
  echo ""
done
exit $EXIT
