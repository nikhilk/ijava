#!/bin/sh

if [ "$1" = "" ]; then
  NOTEBOOKS_DIR=$PWD
else
  NOTEBOOKS_DIR=$1
fi

docker run -i -p 8080:8080 -v $NOTEBOOKS_DIR:/notebooks -t ijava

