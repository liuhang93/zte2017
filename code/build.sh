#!/bin/bash
# Function:
#   Compile and package our java program
# Version:
#   2017/05/01 by LiuHang
basepath=$(cd `dirname $0`; pwd)
APP_HOME=$basepath

if [ ! -d "$APP_HOME/bin" ]; then
  mkdir "$APP_HOME/bin"
fi

#编译
echo building...
MAKE_FILE=$APP_HOME/MakeList.txt
cd "$APP_HOME/src/main/java"
javac -source 1.8 -target 1.8 -d $APP_HOME/bin -encoding UTF-8 @$MAKE_FILE

#打包
echo make jar...
cd "$APP_HOME/bin"
JAR_NAME=$APP_HOME/zte2017.jar
jar -cvf $JAR_NAME *

cd $APP_HOME

echo build jar success!
exit
