package route;

import util.TimeUtil;

import java.util.*;

/**
 * Created by liuhang on 2017/5/2.
 * 重构TSP算法
 */
public class TSP {
    public static final int weightMax = 100;//权重最大值
    private static final int MAX_VALUE = 100000;//一个很大值

    private static Queue<SolutionNode> queue = new PriorityQueue<>(3, new MyComparator());
    private static List<List<Integer>> allRoutes = new ArrayList<>(); //一次ap之后,存放所有的环
    private static int[][] target = new int[GraphCompressed.vertexNum][GraphCompressed.vertexNum];//ap的目标矩阵
    private static int shortestRingIndex;//一次ap后,所有环中顶点数最少的环的索引
    private static int upBound;//分支定界的上界
    private static long bestTime;//更新上界时的时间
    private static List<Integer> bestRoute;//存放当前权值最低路径
    private static int punishment = 1;//对所有的边进行惩罚,使得求出路径的节点数更少
    public static SolutionNode currentSolutionNode;//从优先队列中取出的解节点


    //分支定界法
    public static List<Integer> branchAndBound() {

        TimeUtil.updateTime();
        boolean onlyOneRing = false;
        upBound = Integer.MAX_VALUE;
        queue.clear();

        currentSolutionNode = new SolutionNode();
        initialTargetMatrix();
        punishment = punishment + 1;//惩罚量递增

        KMCompressed.AP(currentSolutionNode, target);
        getRoute(currentSolutionNode);
        if (currentSolutionNode.ringNum == 1) {
            if (currentSolutionNode.apSum < upBound) {
                upBound = currentSolutionNode.apSum;
            }
            bestRoute = allRoutes.get(0);
//            System.out.println("跳出0:直接ap就为一个环");//调试信息,方便看运行过程
//            printRoutes();
            Collections.reverse(bestRoute);
            return bestRoute;
        }
        //进行分支,选择最短的环,破开,修改目标矩阵
        getRequiredAndForbidden(currentSolutionNode);

        //用优先队列,遍历节点
        while (!queue.isEmpty()) {
            if (onlyOneRing && (TimeUtil.getTimeDelay() - bestTime >= Math.sqrt(GraphCompressed.vertexNum
            ) + GraphCompressed.vertexNum)) {
//                System.out.println("跳出1: 最优解长时间未更新,跳出");//调试信息,方便看运行过程
                break;
            }
            currentSolutionNode = queue.poll();
            if (currentSolutionNode.apSum > upBound) {
//                System.out.println("跳出2:" + upBound + ": 优先队列取出的解的apSum值已经大于上界,已经找出最优解");//调试信息,方便看运行过程
                break;
            }
            int[][] costMatrix = modifyTargetMatrix(currentSolutionNode);
            KMCompressed.AP1(currentSolutionNode, costMatrix);
            getRoute(currentSolutionNode);
            if (currentSolutionNode.apSum > upBound) {
                continue;
            }
            if (currentSolutionNode.ringNum == 1) {
                onlyOneRing = true;
                if (currentSolutionNode.apSum < upBound) {
                    upBound = currentSolutionNode.apSum;
                    bestTime = TimeUtil.getTimeDelay();//记录求出解得时间
                    bestRoute = allRoutes.get(0);
                }
                continue;
            }
            getRequiredAndForbidden(currentSolutionNode);
        }
        Collections.reverse(bestRoute);
        return bestRoute;
    }

    //初始化目标矩阵
    private static void initialTargetMatrix() {
        for (int i = 0; i < GraphCompressed.vertexNum; i++) {
            for (int j = 0; j < GraphCompressed.vertexNum; j++) {
                if (i != j)
                    target[i][j] = weightMax - GraphCompressed.edgeWeight[i][j];
                else
                    target[i][j] = weightMax - MAX_VALUE;
            }
        }
        // 重点到起点赋值
        int start = GraphCompressed.startAanEnd[0];
        int end = GraphCompressed.startAanEnd[1];
        target[end][start] = weightMax;
    }

    //获取破开的边,并加入队列
    private static void getRequiredAndForbidden(SolutionNode solutionNode) {
        List<Integer> route = allRoutes.get(shortestRingIndex);
        solutionNode.forbiddenNum++;
        for (int i = 1; i < route.size(); i++) {
            solutionNode.forbidden[solutionNode.forbiddenNum - 1][0] = route.get(i);
            solutionNode.forbidden[solutionNode.forbiddenNum - 1][1] = route.get(i - 1);

            if (i > 1) {
                solutionNode.required[solutionNode.requiredNum][0] = route.get(i - 1);
                solutionNode.required[solutionNode.requiredNum][1] = route.get(i - 2);
                solutionNode.requiredNum++;
            }
            // 新建子节点,完全复制父节点
            SolutionNode subSolutionNode = new SolutionNode();
            subSolutionNode.apSum = solutionNode.apSum;
            subSolutionNode.ringNum = solutionNode.ringNum;
            subSolutionNode.link = Arrays.copyOf(solutionNode.link, solutionNode.link.length);
            subSolutionNode.requiredNum = solutionNode.requiredNum;
            subSolutionNode.forbiddenNum = solutionNode.forbiddenNum;
            for (int j = 0; j < solutionNode.forbidden.length; j++) {
                subSolutionNode.forbidden[j] = Arrays.copyOf(solutionNode.forbidden[j], solutionNode.forbidden[j].length);

            }
            for (int j = 0; j < solutionNode.required.length; j++) {
                subSolutionNode.required[j] = Arrays.copyOf(solutionNode.required[j], solutionNode.required[j].length);
            }
            subSolutionNode.lx = Arrays.copyOf(solutionNode.lx, solutionNode.lx.length);
            subSolutionNode.ly = Arrays.copyOf(solutionNode.ly, solutionNode.ly.length);

            //加入队列
            queue.add(subSolutionNode);
        }

    }

    //对目标矩阵ap(指派)后,对所有必须经过的节点,求环的数目和指派后的权值
    private static void getRoute(SolutionNode solutionNode) {
        allRoutes.clear();
        int ringNum = 0;//环数
        int ringLength = GraphCompressed.vertexNum;
        boolean[] visit = new boolean[GraphCompressed.vertexNum];
        for (int i = 0; i < GraphCompressed.vertexNum; i++) {
            List<Integer> route = new ArrayList<>();
            int v = GraphCompressed.startAanEnd[i];
            if (visit[v]) {
                continue;
            }
            route.add(v);
            int m = solutionNode.link[v];
            while (m > -1) {
                if (visit[m]) {
                    break;
                }
                route.add(m);
                visit[m] = true;
                m = solutionNode.link[m];
            }

            if (route.size() < ringLength) {
                ringLength = route.size();
                shortestRingIndex = ringNum;
            }
            ringNum++;
            allRoutes.add(route);
        }
        solutionNode.ringNum = ringNum;
    }

    //根据破开的环(forbidden)与必须留在路径中的环,来修改ap问题中的目标矩阵
    private static int[][] modifyTargetMatrix(SolutionNode solutionNode) {
        int[][] modifiedTargetMatrix = new int[GraphCompressed.vertexNum][GraphCompressed.vertexNum];
        for (int i = 0; i < target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                modifiedTargetMatrix[i][j] = target[i][j];
            }
        }
        for (int i = 0; i < solutionNode.forbiddenNum; i++) {
            int id1 = solutionNode.forbidden[i][0];
            int id2 = solutionNode.forbidden[i][1];
            modifiedTargetMatrix[id1][id2] -= MAX_VALUE;
        }

        for (int i = 0; i < solutionNode.requiredNum; i++) {
            int id1 = solutionNode.required[i][0];
            int id2 = solutionNode.required[i][1];
            for (int k = 0; k < GraphCompressed.vertexNum; k++) {
                if (k != id2) {
                    modifiedTargetMatrix[id1][k] -= MAX_VALUE;
                }
            }
        }
        return modifiedTargetMatrix;
    }



}
