package binPacking;

import java.util.ArrayList;
import java.util.List;

public class BruteForce extends AbstractBinPacking {

    private List<Bin> bins = new ArrayList<Bin>();
    private int currentBestSolution;
    private List<Bin> currentBestBins;

    /**
     * brute force solution to bin packing problem. The Bin Packing problem is
     * NP-hard, so this algorithm will take a long time to complete.
     *
     * @param in
     * @param binSize
     */
    public BruteForce(List<Integer> in, int binSize) {
        super(in, binSize);
        this.currentBestBins = new ArrayList<Bin>();
        // create maximum of needed bins
        for (Integer in1 : in) {
            bins.add(new Bin(binSize));
        }
        // worst case solution: every item one bin
        currentBestSolution = in.size();
    }

    @Override
    public int getResult() {
        // call actual bruteforce method, with input, starting with position 0
        bruteforce(in, 0);

        return currentBestSolution;
    }

    /**
     * brute force solution to bin packing problem.
     *
     * @param in list of items to be packed
     * @param currentPosition position in input list
     */
    private void bruteforce(List<Integer> in, int currentPosition) {
        if (currentPosition >= in.size()) { // reached last item, done with this iteration
            int filledBins = getFilledBinsCount();
            if (filledBins < currentBestSolution) { // is this solution better than the current best?
                currentBestSolution = filledBins; // then save it
                currentBestBins = deepCopy(bins);
            }
            return;
        }
        // iterate over bins
        Integer currentItem = in.get(currentPosition);
        for (Bin bin : bins) {
            if (bin.put(currentItem)) {
                bruteforce(in, currentPosition + 1);
                bin.remove(currentItem);
            } // else: item did not fit in bin, ignore
        }
    }

    /**
     * returns how many bins currently have at least one item inside them.
     *
     * @return
     */
    private int getFilledBinsCount() {
        int filledBins = 0;
        for (Bin bin : bins) {
            if (bin.numberOfItems() != 0) {
                filledBins++;
            }
        }
        return filledBins;
    }

    @Override
    public void printBestBins() {
        System.out.println("Bins:");
        if (currentBestSolution == in.size()) {
            System.out.println("each item is in its own bin");
        } else {
            for (Bin currentBin : currentBestBins) {
                if (currentBin.numberOfItems() != 0) { // don't print empty bins
                    System.out.println(currentBin.toString());
                }
            }
        }
    }
}
