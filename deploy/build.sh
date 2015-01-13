#!/bin/sh

cp -R ../build .
docker build -t ijava .

rm -rf build

