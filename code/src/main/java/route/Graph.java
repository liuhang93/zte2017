package route;

import util.TimeUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by liuhang on 2017/4/20.
 */
public class Graph {
    public static final int vertexNumMax = 5100;//顶点数最大值(包括起点终点)
    public static final int vertexDemandNumMax = 5000; //必须经过的顶点数的最大值(两个取食物的地方,玉米间,水果间,加上起点和终点)

    public static int vertexNum;//图的顶点数目
    public static int vertexDemandNum;//图中必须经过的顶点数目(包含起点终点)
    public static int edgeDemandNum;//必须经过的边数
    public static int edgeForbiddenNum;//禁止经过的边数

    public static int[][] edgeWeight = new int[vertexNumMax][vertexNumMax];//边权重,edgeWeight[i][j]表示顶点i和j之间的边的权重
    public static Node[] nodes = new Node[vertexNumMax];//所有顶点集合

    public static int[] vertexDemand = new int[vertexDemandNumMax];//必须经过的顶点(前两个为起点和终点)

    public static int maxStepNum;//最多经过几个结点
    public static HashSet<String> forbiddenEdges = new HashSet<>();//禁止边集合
    public static HashSet<String> requiredEdges = new HashSet<>();//必经边集合

    public static void makeGraph(String[] graphContent, String[] condition) {

        readCondition(condition);        //读取限制条件
        readGraph(graphContent);        //读取图信息

        //必经节点,状态设置为1
        for (int i = 0; i < vertexDemandNum; i++) {
            int v = vertexDemand[i];
            nodes[v].state = 1;
        }

        //必经边,把必经边的两个端点设置必经点
        for (String string : requiredEdges) {
            String[] strings = string.split(",");
            int v1 = Integer.parseInt(strings[0]);
            int v2 = Integer.parseInt(strings[1]);
            if (nodes[v1].state != 1) {
                vertexDemand[vertexDemandNum++] = v1;
                nodes[v1].state = 1;
            }
            if (nodes[v2].state != 1) {
                vertexDemand[vertexDemandNum++] = v2;
                nodes[v2].state = 1;
            }
        }
        TimeUtil.printLog("读图完成,节点数:" + vertexNum + ";必经点数:" + vertexDemandNum + ";必经边数:" + edgeDemandNum);
    }

    public static void readCondition(String[] condition) {
        //解析第一行: 起点和终点
        String[] line1 = condition[0].split(",");
        int vertexStart = Integer.parseInt(line1[0]);
        int vertexEnd = Integer.parseInt(line1[1]);
        vertexDemand[vertexDemandNum++] = vertexStart;
        vertexDemand[vertexDemandNum++] = vertexEnd;

        //解析第二行: 必须经过哪几个结点
        String[] line2 = condition[1].split("[,]");
        for (String string : line2) {
            int v = Integer.parseInt(string);
            vertexDemand[vertexDemandNum++] = v;
        }

        //解析第三行: 必须经过的边
        if (!condition[2].equals("NA")) {
            String[] line3 = condition[2].split("[|]");
            for (String string : line3) {
                String[] v = string.split(",");
                int v0 = Integer.parseInt(v[0]);
                int v1 = Integer.parseInt(v[1]);
                requiredEdges.add(v0 + "," + v1);
                edgeDemandNum++;
            }
        }

        //解析第四行: 禁止经过的边
        if (!condition[3].equals("NA")) {
            String[] line4 = condition[3].split("[|]");
            for (String string : line4) {
                String[] v = string.split(",");
                int v0 = Integer.parseInt(v[0]);
                int v1 = Integer.parseInt(v[1]);
                forbiddenEdges.add(v0 + "," + v1);
                edgeForbiddenNum++;
            }
        }

        //解析第五行: 最多走几步(几个结点)
        maxStepNum = Integer.parseInt(condition[4]);
    }


    public static void readGraph(String[] graphContent) {
        for (int i = 0; i < graphContent.length; i++) {
            String edge = graphContent[i];
            String[] edgeContent = edge.split(",");
            int v1 = Integer.parseInt(edgeContent[1]);
            int v2 = Integer.parseInt(edgeContent[2]);
            int weight = Integer.parseInt(edgeContent[3]);

            //去掉禁止边
            if (forbiddenEdges.contains(v1 + "," + v2) || forbiddenEdges.contains(v2 + "," + v1)) {
                continue;
            }

            edgeWeight[v1][v2] = weight;
            edgeWeight[v2][v1] = weight;

            if (nodes[v1] == null) {
                nodes[v1] = new Node();
                vertexNum++;
            }
            nodes[v1].link[nodes[v1].linkNum++] = v2;
            if (nodes[v2] == null) {
                nodes[v2] = new Node();
                vertexNum++;
            }
            nodes[v2].link[nodes[v2].linkNum++] = v1;
        }

        //每个顶点的最后一个出度,指向自己
        for (int i = 0; i < vertexNum; i++) {
            nodes[i].link[nodes[i].linkNum] = i;
        }
    }
}

class Node {
    public int state;//顶点状态;1表示必经
    public int linkNum;//顶点的度
    public int[] link = new int[1000];//顶点连接的点集合(度不超过30)
}
