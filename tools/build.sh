#!/bin/sh

mkdir -p ../build
mvn clean package -DskipTests -l ../build/maven.log -f ../sources

if [ "$?" = "0" ]; then
  cp ../sources/api/target/api-0.1.jar ../build/ijavart.jar

  cat ../sources/stub ../sources/core/target/core-0.1.jar > ../build/ijava
  chmod +x ../build/ijava
  ls -l ../build
else
  cat ../build/maven.log
fi
