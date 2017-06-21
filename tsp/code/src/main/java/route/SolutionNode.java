package route;

import java.util.Comparator;

/**
 * Created by liuhang on 2017/2/6.
 * 解空间的结点
 */
public class SolutionNode implements Cloneable {

    public static final int forbiddenMax = 600;
    public static final int requiredMax = 1300;

    public int ringNum;//环的数量
    public int apSum;//ap问题的解的权值
    public int[] link;//ap问题的解(路径匹配)

    public int[][] forbidden = new int[forbiddenMax][2];
    public int[][] required = new int[requiredMax][2];

    public int forbiddenNum;
    public int requiredNum;

    //public Map<Integer, Integer> forbidden = new HashMap<>();//必须不可出现在路径中的边
    //public Map<Integer, Integer> required = new HashMap<>();//必须出现在路径中的边

    public int[] lx;
    public int[] ly;

}

class MyComparator implements Comparator<SolutionNode> {
    @Override
    public int compare(SolutionNode o1, SolutionNode o2) {

        if (TSP.currentSolutionNode.ringNum > 5) {
            return o1.ringNum * o1.apSum - o2.apSum * o2.ringNum;
        }
//        return o1.apSum - o2.apSum;
        if (o1.apSum != o2.apSum) {
            return o1.apSum - o2.apSum;

//            return o1.ringNum - o2.ringNum;
        } else {
            return o1.ringNum - o2.ringNum;

//            return o1.apSum - o2.apSum;
        }
//        return o1.ringNum - o2.ringNum;

    }
}