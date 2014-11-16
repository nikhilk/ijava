#!/bin/sh

mkdir -p target

javac -classpath ../build/ijava.jar -d target sample/Shell.java
jar cvf ../build/sample.jar -C target sample

