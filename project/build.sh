#!/bin/sh

mkdir -p ../build
mvn clean package -DskipTests -q

cp core/target/core-0.1.jar ../build/ijava.jar

cat ijava.sh app/target/app-0.1.jar > ../build/ijava
chmod +x ../build/ijava
ls -l ../build/ijava

