#!/bin/bash
#author py
#date 2018-07-05

mainClass=org.shersfy.server.boot.SocketServerApplication
pid=`ps aux | grep $mainClass | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ];then
  kill -9 $pid
  echo "WebServer stopped success"
else
  echo "No WebServer process exists"
fi
