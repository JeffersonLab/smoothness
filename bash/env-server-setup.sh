#!/bin/bash

if [ -z "$1" ]
  then
    echo "Provide name of .env override file to use"
    exit 1
fi

. env/default.env

. env/$1

. server-setup.sh
