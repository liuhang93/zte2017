#!/bin/bash
# Function:
#   run the jar package by script
# Version:
#   2017/05/01 by LiuHang
basepath=$(cd `dirname $0`; pwd)
APP_HOME=$basepath

JAVA=$JAVA_HOME/bin/java

JVM_OPT="-classpath"
JVM_OPT="$JVM_OPT $APP_HOME/zte2017.jar"

graphFilePath=$1
conditionFilePath=$2
resultFilePath=$3
$JAVA $JVM_OPT route.Main $graphFilePath $conditionFilePath $resultFilePath 2>&1
exit