#!/bin/sh

cp -R ../build .
docker build -t ijava ../build 

rm -rf build

