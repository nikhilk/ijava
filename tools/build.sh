#!/bin/sh

mkdir -p ../build/outputs
mvn clean package -DskipTests -l ../build/outputs/maven.log -f ../sources

if [ "$?" = "0" ]; then
  cp ../build/outputs/api/api-0.1.jar ../build/ijavart.jar
  cp ../build/outputs/extensions/charting/ijavaext-charting-0.1.jar ../build/ijavaext-charting.jar

  cat ../sources/stub ../build/outputs/core/core-0.1.jar > ../build/ijava
  chmod +x ../build/ijava
  ls -l ../build
else
  cat ../build/outputs/maven.log
fi
