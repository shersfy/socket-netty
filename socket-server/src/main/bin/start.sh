#!/bin/bash
#author py
#date 2018-07-05

cd "$(dirname "$0")"
cd ..

javahome=${JAVA_HOME}
JAVA_OPTS="-server -Xms512m -Xmx512m -Xmn200m"
if [ -z $javahome ]; then
    echo error: JAVA_HOME not configured
    exit 1
fi
echo "JAVA_HOME=$javahome"

mainClass=com.cmhy.boot.WebServer
pid=`ps aux | grep $mainClass | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ];then
  echo error: WebServer already exists
  exit 1
fi
if [ ! -d "./logs" ]; then
  mkdir ./logs
fi
echo "Welcome to ACCEL-PPP"
nohup $javahome/bin/java $JAVA_OPTS -cp libs/*:. $mainClass > logs/stdout.log 2>&1 &
echo "`date '+%Y-%m-%d %H:%M:%S'` WebServer is running..."
