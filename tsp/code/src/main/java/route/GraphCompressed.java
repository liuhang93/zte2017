package route;

import util.TimeUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhang on 2017/5/2.
 * 对图进行压缩处理
 */
public class GraphCompressed {
    public static int vertexNum = Graph.vertexDemandNum + Graph.edgeDemandNum;//压缩后图的顶点数目
    public static Node[] nodes = new Node[vertexNum];//所有顶点集合
    public static int[][] edgeWeight = new int[vertexNum][vertexNum];//新图费用矩阵
    public static int[] vertexId = new int[Graph.vertexNum];//原始图和新图映射，vertexId[i]表示原图中顶点i在信息中的编号
    public static int[] idToVertex = new int[vertexNum];//idToVertex[i]=k,表示新图顶点i在原图中的编号k
    public static int[] startAanEnd = new int[vertexNum];//把起点终点放在前面
    public static HashSet<Integer> newNodes = new HashSet<>();//新增的节点,在必经边中间新增的节点

    //将图压缩到只含有必经节点
    public static void compressGraph(int add) {
        Dijkstra.getAllShortPath(add);
        Map<String, List<Integer>> compressedMap = Dijkstra.compressedMap;
        for (int i = 0; i < vertexNum; i++) {
            for (int j = 0; j < vertexNum; j++) {
                edgeWeight[i][j] = 0;
            }
        }
        newNodes.clear();//清空！
        Arrays.fill(vertexId, -1);
        int count = 0;
        for (Map.Entry<String, List<Integer>> entry : compressedMap.entrySet()) {
            String str = entry.getKey();
            String[] strs = str.split("-");
            int id1 = Integer.parseInt(strs[0]);
            int id2 = Integer.parseInt(strs[1]);
            int w = Integer.parseInt(strs[2]);

            if (vertexId[id1] == -1) {
                vertexId[id1] = count;
                nodes[count] = new Node();
                idToVertex[count] = id1;
                count++;
            }
            if (vertexId[id2] == -1) {
                vertexId[id2] = count;
                nodes[count] = new Node();
                idToVertex[count] = id2;
                count++;
            }
            int index1 = vertexId[id1], index2 = vertexId[id2];
            edgeWeight[index1][index2] = w;
            edgeWeight[index2][index1] = w;


            //必经边，中间加一个必经点
            if (Graph.requiredEdges.contains(id1 + "," + id2) || Graph.requiredEdges.contains(id2 + "," + id1)) {
                nodes[count] = new Node();
                newNodes.add(count);

                nodes[count].link[nodes[count].linkNum++] = index1;
                nodes[count].link[nodes[count].linkNum++] = index2;
                nodes[index1].link[nodes[index1].linkNum++] = count;
                nodes[index2].link[nodes[index2].linkNum++] = count;

                edgeWeight[count][index1] = w / 2;
                edgeWeight[index1][count] = w / 2;
                edgeWeight[count][index2] = w - edgeWeight[count][index1];
                edgeWeight[index2][count] = w - edgeWeight[count][index1];
                count++;
            } else {
                nodes[index1].link[nodes[index1].linkNum++] = index2;
                nodes[index2].link[nodes[index2].linkNum++] = index1;
            }
        }

        Arrays.fill(startAanEnd, -1);
        startAanEnd[0] = vertexId[Graph.vertexDemand[0]];//起点
        startAanEnd[1] = vertexId[Graph.vertexDemand[1]];//终点
        int j = 2;
        for (int i = 0; i < vertexNum; i++) {
            if (i != startAanEnd[0] && i != startAanEnd[1]) {
                startAanEnd[j++] = i;
            }
        }
        nodes[startAanEnd[1]].linkNum = 0;
        nodes[startAanEnd[1]].link[nodes[startAanEnd[1]].linkNum++] = startAanEnd[0];


//        System.out.println("压缩图完成");
//        List<Integer> route = TSP.branchAndBound();
//        List<Integer> path = Route.parsePath(route);
//        System.out.println("压缩后tsp");
//        TimeUtil.printLog("图压缩完成，压缩后节点数:" + vertexNum);
    }
}
