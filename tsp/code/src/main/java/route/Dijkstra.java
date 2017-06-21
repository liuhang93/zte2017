package route;


import java.util.*;

/**
 * Created by liuhang on 2017/5/1.
 * 堆优化的Dijkstra算法,求已有图的最短路径
 */
public class Dijkstra {
    static final int n = Graph.vertexNum;//顶点数
    static int[] dist = new int[n];//当前点与所有点的最短路径
    static int[] prev = new int[n];//前驱数组,start到顶点i的路径,位于i之前的那个顶点
    static Queue<DijkNode> queue = new PriorityQueue<>(new Comparator<DijkNode>() {
        @Override
        public int compare(DijkNode o1, DijkNode o2) {
            return o1.dist - o2.dist;
        }
    }); //优先队列,存已经扩展的节点
    static int[][] weight = new int[n][n];
    static int[][] weightTemp = new int[n][n];//weight备份

    static {
        for (int i = 0; i < n; i++) {
            weight[i] = Arrays.copyOf(Graph.edgeWeight[i], n);
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                weightTemp[i][j] = weight[i][j];
            }
        }
    }

    public static Map<String, List<Integer>> compressedMap = new HashMap<>();

    public static void dijkstra(int start) {
        for (int i = 0; i < n; i++) {
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
        }
        dist[start] = 0;
        queue.add(new DijkNode(start, 0));
        while (!queue.isEmpty()) {
            DijkNode temp = queue.poll();
            if (temp.dist != dist[temp.vertex]) {
                continue;
            }//跳过已经失效的点(更新路径时,dist数组已经更新,但优先队列中对应的dist还没有更新,与dist数组值不同,说明失效)
            int v = temp.vertex;
            for (int i = 0; i < Graph.nodes[v].linkNum; i++) {
                int t = Graph.nodes[v].link[i];
                if (dist[v] + weight[v][t] < dist[t]) {
                    dist[t] = dist[v] + weight[v][t];
                    queue.add(new DijkNode(t, dist[t]));
                    prev[t] = v;
                }

            }
        }
    }

    public static void getShortPath(int start) {
        dijkstra(start);
        for (int j = 0; j < Graph.vertexDemandNum; j++) {
            int end = Graph.vertexDemand[j];
            if (end == start) continue;
            if (compressedMap.containsKey(start + "-" + end + "-" + dist[end]) || compressedMap.containsKey(end + "-" + start + "-" + dist[end])) {
                continue;
            }
            ArrayList<Integer> route = new ArrayList<>();
            route.add(end);
            int i = end;
            while (prev[i] != -1) {
                route.add(prev[i]);
                i = prev[i];
            }
            Collections.reverse(route);
            compressedMap.put(start + "-" + end + "-" + dist[end], route);
        }
    }

    public static void getAllShortPath(int add) {
        compressedMap.clear();
        if (add != 0) {
            increaseEdgeWeight(add);
        }
        for (int i = 0; i < Graph.vertexDemandNum; i++) {
            getShortPath(Graph.vertexDemand[i]);
        }
    }

    private static void increaseEdgeWeight(int add) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < Graph.nodes[i].linkNum; j++) {
                int k = Graph.nodes[i].link[j];
                if (add < 0)
                    weight[i][k] = 1;
                else {
                    weight[i][k] = weightTemp[i][k] + add;
                    weightTemp[i][k] = weight[i][k];
                }

            }
        }
    }

}

class DijkNode {
    int vertex;
    int dist;

    public DijkNode(int vertex, int dist) {
        this.vertex = vertex;
        this.dist = dist;
    }
}