package binPacking;

import java.util.Arrays;
import java.util.List;

public class BinPacking {

    public static void main(String[] args) {
        List<Integer> in = Arrays.asList(49,41,34,33,29,26,26,22,20,19);
//        List<Integer> in = Arrays.asList(70,60,50,33,33,33,11,7,3);
//        List<Integer> in = Arrays.asList(74,49,34,29,26,26,22,20,19);

//        BruteForce bf = new BruteForce(in, 100);
//        testBinPacking(bf, "brute force");

        FirstFitDecreasing ffd = new FirstFitDecreasing(in, 100);
        testBinPacking(ffd, "first fit decreasing");
    }

    private static void testBinPacking(AbstractBinPacking algo, String algoName) {
        long startTime;
        long estimatedTime;

        startTime = System.currentTimeMillis();
        System.out.println("needed bins (" + algoName + "): " + algo.getResult());
        algo.printBestBins();
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("in " + estimatedTime + " ms");

        System.out.println("\n\n");
    }

}
