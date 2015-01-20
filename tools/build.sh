#!/bin/sh

mkdir -p ../build/app

cp ../deploy/Dockerfile ../build/Dockerfile
cp ../deploy/start.sh ../build/app/start.sh
cp -R ../ipython/* ../build/app
mvn clean package -DskipTests -l ../build/maven.log -f ../sources

if [ "$?" = "0" ]; then
  cp ../sources/api/target/api-0.1.jar ../build/app/ijavart.jar

  cat ../sources/stub ../sources/core/target/core-0.1.jar > ../build/app/ijava
  chmod +x ../build/app/ijava
  ls -l ../build/*
else
  cat ../build/maven.log
fi
