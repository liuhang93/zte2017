#!/bin/bash
# Function:
#   Batch test of program
#   run: sh batchTest.sh /xxx/**/case
# Version:
#   2017/05/23 by LiuHang
[ $# -ne 1 ] && echo "please input case directory: /xxx/**/case" && exit 0
basepath=$(cd `dirname $0`; pwd)
run_script="$basepath/run.sh"
case_dir=$1
cd $case_dir
if [ -f "${basepath}/log.txt" ];then
    rm -f "${basepath}/log.txt"
fi
for  case in $(find . -type d)
do
    topo="$case/topo.csv"
    demand="$case/demand.csv"
    result="$case/result.csv"
    if [ -f $topo ] && [ -f $demand ]; then
        $run_script $topo $demand $result >> "${basepath}/log.txt"
        echo "\n" >> "${basepath}/log.txt"
    fi
done