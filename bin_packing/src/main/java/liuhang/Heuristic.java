package liuhang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhang on 2017/6/11.
 * 启发式算法
 */
public class Heuristic extends AbstractBinPacking {

    private List<Integer> attribute1 = new ArrayList<>();
    private List<Integer> attribute2 = new ArrayList<>();

    private List<Bin> bins = new ArrayList<>();


    public Heuristic(List<Item> in, Item binSize) {
        super(in, binSize);
    }

    public void executeHeuristic() {
        int k = 0;
        while (!in.isEmpty()) {
            k++;
            FirstFitDecreasing currentFfd = new FirstFitDecreasing(in, binSize);
            updateAttributes();
            NextFit1D currentNf1d1 = new NextFit1D(attribute1, binSize.getAttribute1());
            NextFit1D currentNf1d2 = new NextFit1D(attribute2, binSize.getAttribute2());
            int p1 = currentNf1d1.executeNF1D();
            int p2 = currentNf1d2.executeNF1D();
            double lambda = (double) p1 / p2;
            List<Bin> currentBins = currentFfd.executeFFD(lambda);
            boolean noWellFilledBin = true;
            double[] ave = getAverageAttributes();
            for (Bin binTemp : currentBins) {
                int a1 = binTemp.currentSize.getAttribute1();
                int a2 = binTemp.currentSize.getAttribute2();
                if (a1 >= ave[0] && a2 >= ave[1]) {
                    in.removeAll(binTemp.items);
                    noWellFilledBin = false;
                    bins.add(binTemp);
                }
            }
            if (noWellFilledBin) {
                bins.addAll(currentBins);
                break;
            }

        }
        System.out.println("迭代次数:" + k);
    }

    private double[] getAverageAttributes() {

        int sum1 = 0;
        int sum2 = 0;
        for (int i = 0; i < attribute1.size(); i++) {
            sum1 += attribute1.get(i);
            sum2 += attribute2.get(i);
        }
        int bin1 = (int) Math.ceil((double) sum1 / binSize.getAttribute1());
        int bin2 = (int) Math.ceil((double) sum2 / binSize.getAttribute2());

        double[] ave = new double[2];
        ave[0] = (double) sum1 / bin1;
        ave[1] = (double) sum2 / bin2;
        return ave;

    }

    private void updateAttributes() {
        attribute1.clear();
        attribute2.clear();
        for (Item item : in) {
            attribute1.add(item.getAttribute1());
            attribute2.add(item.getAttribute2());
        }
    }

    public void printBestBins() {
        System.out.println("Bins(size:" + bins.size() + "):");

        for (Bin bin : bins) {
            System.out.println(bin.toString());
        }
        System.out.println();

    }
}
