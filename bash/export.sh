#!/bin/bash

if [ -z "$1" ]
  then
    echo "No file path provided"
    exit 1
fi

if [ ! -f "$1" ]; then
    echo "$1 does not exist."
    exit 1
fi

set -a; . $1; set +a

