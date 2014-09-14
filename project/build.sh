#!/bin/sh

mkdir -p ../build
mvn clean package

cat ijava.sh app/bin/ijava-app-0.1.jar > ../build/ijava
chmod +x ../build/ijava
ls -l ../build/ijava

