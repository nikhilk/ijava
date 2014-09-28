#!/bin/sh
# Setup Java Debugging...

export java_args="-Xdebug -Xrunjdwp:transport=dt_socket,address=127.0.0.1:8888,server=y,suspend=n"

