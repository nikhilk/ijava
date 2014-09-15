#!/bin/sh

mkdir -p ../build
mvn clean package -DskipTests -q

cat ijava.sh kernel/target/kernel-0.1.jar > ../build/ijava
chmod +x ../build/ijava
ls -l ../build/ijava

