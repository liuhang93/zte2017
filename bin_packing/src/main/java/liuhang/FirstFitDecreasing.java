package liuhang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by liuhang on 2017/6/10.
 */
public class FirstFitDecreasing extends AbstractBinPacking {

    private List<Bin> bins = new ArrayList<>();

    public FirstFitDecreasing(List<Item> in, Item binSize) {
        super(in, binSize);
    }

    public List<Bin> executeFFD(double lambda) {
        Collections.sort(in, new myComparator(lambda));
        bins.add(new Bin(binSize));
        for (Item currentItem : in) {
            boolean putItem = false;
            int currentBinIndex = 0;
            while (!putItem) {
                if (currentBinIndex == bins.size()) {
                    Bin newBin = new Bin(binSize);
                    newBin.put(currentItem);
                    bins.add(newBin);
                    putItem = true;
                } else if (bins.get(currentBinIndex).put(currentItem)) {
                    putItem = true;
                } else {
                    currentBinIndex++;
                }
            }
        }
        return bins;
    }

    public void printBestBins() {
        System.out.println("Algorithm: FFD; Bins(size:" + bins.size() + "):");

        for (Bin bin : bins) {
            System.out.println(bin.toString());
        }

        System.out.println();

    }

}

class myComparator implements Comparator<Item> {
    private double lambda;

    public myComparator(double lambda) {
        this.lambda = lambda;
    }

    @Override
    public int compare(Item o1, Item o2) {
        double priority1 = 0;
        double priority2 = 0;
        if (lambda < 0) {
            priority1 = Math.max(o1.getAttribute1(), o1.getAttribute2());
            priority2 = Math.max(o2.getAttribute1(), o2.getAttribute2());
        } else {
            priority1 = this.lambda * o1.getAttribute1() + o1.getAttribute2();
            priority2 = this.lambda * o2.getAttribute1() + o2.getAttribute2();
        }

        return (priority2 - priority1) > 0 ? 1 : -1;
    }
}