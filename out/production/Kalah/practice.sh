#!/bin/sh
set -e
javac -cp .:jkgp.jar Agent.java
USE_WEBSOCKET=t exec java -cp .:jkgp.jar Agent
