#!/bin/bash

while true; do
read -rsn1 input
if [ "$input" = "ö" ]; then
    (
      cd ..
      cifuzz remote-run
      echo "Executing testing."
    )
fi
done