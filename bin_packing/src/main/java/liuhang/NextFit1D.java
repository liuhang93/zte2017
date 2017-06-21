package liuhang;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liuhang on 2017/6/11.
 * 1-d bin packing, next fit
 */
public class NextFit1D {
    public List<Integer> in;
    public int binSize;

    public NextFit1D(List<Integer> in, int binSize) {
        this.in = in;
        this.binSize = binSize;
    }

    public int executeNF1D() {
        int size = binSize;
        int count = 1;
        for (int i = 0; i < in.size(); i++) {
            int item = in.get(i);
            if (size - item >= 0) {
                size -= item;
            } else {
                count++;
                size = binSize;
                i--;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        List<Integer> in = Arrays.asList(4, 5, 8, 3, 4, 5, 1, 6);
        NextFit1D nextFit1D = new NextFit1D(in, 10);
        System.out.println(nextFit1D.executeNF1D());
    }
}


