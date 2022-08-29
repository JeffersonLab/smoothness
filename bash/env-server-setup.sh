#!/bin/bash

DIRNAME=`dirname "$0"`
SCRIPT_HOME=`cd -P "$DIRNAME"; pwd`

. ${SCRIPT_HOME}/env/default.env

if [ -z "$1" ]
then
. ${SCRIPT_HOME}/env/$1
fi

. ${SCRIPT_HOME}/server-setup.sh
