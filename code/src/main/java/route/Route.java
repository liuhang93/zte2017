package route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liuhang on 2017/5/9.
 */
public class Route {
    public static String[] searchRoute(String[] graphContent, String[] condition) {
        Graph.makeGraph(graphContent, condition);
        GraphCompressed.compressGraph(0);
        List<Integer> route = TSP.branchAndBound();
        List<Integer> path = parsePath(route);
        boolean hasResult = true;
        List<Integer> pathTemp = new ArrayList<>();
        //判断是否有解
        if (path.size() > Graph.maxStepNum) {
            GraphCompressed.compressGraph(-1);//所有边置为1
            List<Integer> routeTemp = TSP.branchAndBound();
            pathTemp = parsePath(routeTemp);
            if (pathTemp.size() > Graph.maxStepNum) {
                hasResult = false;//确定无解
//                System.out.println("无解;size:" + path.size());
            }
        }

        //有解时进行迭代
        if (hasResult) {
            while (path.size() > Graph.maxStepNum) {
                GraphCompressed.compressGraph(1);
                route = TSP.branchAndBound();
                path = parsePath(route);
            }
        }
        String[] result = new String[3];
        if (hasResult) {
            result[0] = "存在满足所有限制条件的解";
            result[1] = saveRoute(path, "最优路径");
            System.out.println(result[0]);
            System.out.println(result[1]);
        } else {
            result[0] = "不存在满足所有限制条件的解：节点数限制不满足，最少节点数为"+pathTemp.size();
            result[1] = saveRoute(pathTemp, "节点数最少的路径");
            result[2] = saveRoute(path, "费用和最少的路径");
            System.out.println(result[0]);
            System.out.println(result[1]);
            System.out.println(result[2]);
        }

        return result;
//        return new String[]{"0|1|2", "5|6|2"};
    }

    private static String saveRoute(List<Integer> route, String log) {
        int sum = 0;
        String str = "";
        for (int i = 0; i < route.size(); i++) {
            int id1 = route.get(i);
            if (i < route.size() - 1) {
                int id2 = route.get(i + 1);
                sum += Graph.edgeWeight[id1][id2];
            }
            str = str + id1 + "|";
        }
        str = str.substring(0, str.length() - 1);
//        System.out.println("路径:" + str);
//        System.out.println("sum:" + sum + ";节点数:" + (route.size()));
        str = log + ":" + str + "；费用和：" + sum + "；节点数：" + route.size();
        return str;
    }

    public static List<Integer> parsePath(List<Integer> route) {
        for (int i = 0; i < route.size() - 1; i++) {
            int v = route.get(i);
            if (GraphCompressed.newNodes.contains(v)) {
                route.remove(i);
            }
        }
        List<Integer> path = new ArrayList<>();
        for (int i = 0; i < route.size() - 2; i++) {
            int id1 = GraphCompressed.idToVertex[route.get(i)];
            int id2 = GraphCompressed.idToVertex[route.get(i + 1)];
            String s1 = id1 + "-" + id2 + "-" + GraphCompressed.edgeWeight[route.get(i)][route.get(i + 1)];
            String s2 = id2 + "-" + id1 + "-" + GraphCompressed.edgeWeight[route.get(i + 1)][route.get(i)];
            if (Dijkstra.compressedMap.containsKey(s1)) {
                List<Integer> temp = Dijkstra.compressedMap.get(s1);
                path.addAll(temp);
                if (i != route.size() - 3)
                    path.remove(path.size() - 1);
            } else if (Dijkstra.compressedMap.containsKey(s2)) {
                List<Integer> temp = Dijkstra.compressedMap.get(s2);
                Collections.reverse(temp);
                path.addAll(temp);
                if (i != route.size() - 3)
                    path.remove(path.size() - 1);
            }
        }
        return path;
    }
}
