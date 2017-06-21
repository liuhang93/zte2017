我使用了第三方库lpsolve5.5.2.5。已经静态编译了。在64位linux下可以直接运行zte.out 。例如：
./zte.out case/case0/topo.csv case/case0/demand.csv case/case0/result.csv

如果出现错误，应该是lpsolve库的问题。可以选择动态编译。步骤如下：
1，解压lp_solve_5.5.2.5_dev_ux64.tar.gz
2，将所有.h文件放在/usr/include目录下。命令为： 
   sudo cp *.h /usr/include
3，将.o和.so文件放在/usr/lib目录下。命令为：
   sudo cp * /usr/lib
4，动态编译：
   g++ main.cpp -llpsolve55

(以上所有代码均在ubuntu 16.04 下跑通)
