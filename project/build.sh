#!/bin/sh

mkdir -p ../build
mvn clean package -DskipTests -q

cp core/target/core-0.1.jar ../build/ijava.jar
cp sample/target/sample-0.1.jar ../build/sample.jar

cat ijava.sh app/target/app-0.1.jar > ../build/ijava
chmod +x ../build/ijava
ls -l ../build

