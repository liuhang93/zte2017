package binPacking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirstFitDecreasing extends AbstractBinPacking {

    private List<Bin> bins = new ArrayList<>();

    public FirstFitDecreasing(List<Integer> in, int binSize) {
        super(in, binSize);
    }

    @Override
    public int getResult() {
        Collections.sort(in, Collections.reverseOrder()); // sort input by size (big to small)
        bins.add(new Bin(binSize)); // add first bin
        for (Integer currentItem : in) {
            // iterate over bins and try to put the item into the first one it fits into
            boolean putItem = false; // did we put the item in a bin?
            int currentBin = 0;
            while (!putItem) {
                if (currentBin == bins.size()) {
                    // item did not fit in last bin. put it in a new bin
                    Bin newBin = new Bin(binSize);
                    newBin.put(currentItem);
                    bins.add(newBin);
                    putItem = true;
                } else if (bins.get(currentBin).put(currentItem)) {
                    // item fit in bin
                    putItem = true;
                } else {
                    // try next bin
                    currentBin++;
                }
            }
        }
        return bins.size();
    }

    @Override
    public void printBestBins() {
        System.out.println("Bins:");
        if (bins.size() == in.size()) {
            System.out.println("each item is in its own bin");
        } else {
            for (Bin bin : bins) {
                System.out.println(bin.toString());
            }
        }
    }
}
