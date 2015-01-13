#!/bin/sh

docker run -i -p 8080:8080 -v $1:/notebooks -t ijava

