package liuhang;

import java.util.*;

/**
 * Created by liuhang on 2017/6/13.
 */
public class BranchAndBound {
    private List<Item> items;// list of input items
    public Item binSize;// size of bin

    private List<Bin> optimalBins = new ArrayList<>();// the bins of optimal solution
    private int lb; // lower bound;
    private int currentSol; // value of current best solution


    private Map<Item, Integer> itemsIndex = new HashMap<>();// index of items
    private boolean[] itemAssignedFlag;// false denotes not assigned
    private int n;//items size;
    private int itemNotAssignedSize;


    private int k; // k = currentSol-1 ;denotes the value of the current solution minus one
    private int r; // current level (r already generated maximal bins present))
    private List<Bin> currentBestBins = new ArrayList<>();// list of bins of current solution
    private List<Bin> binsTemp = new ArrayList<>();// temp list of bin when the procedure is running

    private Map<Integer, List<Bin>> dominates = new HashMap<>();// set of discarded bins at level r


    public BranchAndBound(List<Item> in, Item binSize) {
        this.items = in;
        this.binSize = binSize;
        currentSol = items.size();
        itemAssignedFlag = new boolean[items.size()];
        itemNotAssignedSize = items.size();
        n = items.size();
    }


    public void executeBranchBound() {

        // sort the list of items;
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o2.getAttribute1() + o2.getAttribute2() - o1.getAttribute1() - o1.getAttribute2();
            }
        });

        // index the items;
        for (int i = 0; i < items.size(); i++) {
            itemsIndex.put(items.get(i), i);
        }

        // get lb;
        lb = getLb(items);
        System.out.println("lb:" + lb);

        while (true) {

            initialization();// step 0;
            Bin brTemp = new Bin(binSize);// generate the first bin.

            while (true) {
                if (brTemp.items.isEmpty()) {
                    getBrFromMB(brTemp);// step 1;
                }

                // remove the items in bin;
                for (Item item : brTemp.items) {
                    int index = itemsIndex.get(item);
                    itemAssignedFlag[index] = true;// index assigned
                }
                itemNotAssignedSize -= brTemp.items.size();

                if (itemNotAssignedSize == 0) {
                    binsTemp.add(brTemp);
                    currentBestBins.clear();
                    currentBestBins.addAll(binsTemp);
                    if (!termination()) {
                        System.out.println(r);
                        break;// go to step 0;
                    } else {
                        System.out.println(r);
                        optimalBins.clear();
                        optimalBins.addAll(currentBestBins);
                        System.out.println("跳出0,达到下界");
                        return;// get optimal; stop.
                    }
                }
                List<Item> itemList = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    if (!itemAssignedFlag[i]) {
                        itemList.add(items.get(i));
                    }
                }
                if (dominanceTest(brTemp) && lowerBoundTest(itemList)) {//step 2
                    binsTemp.add(brTemp);

                    // update dominates
                    if (!dominates.containsKey(r)) {
                        ArrayList<Bin> discardedBin = new ArrayList<>();
                        Bin binTemp = new Bin(binSize);
                        binTemp.items.addAll(brTemp.items);
                        binTemp.currentSize.setAttribute1(brTemp.currentSize.getAttribute1());
                        binTemp.currentSize.setAttribute2(brTemp.currentSize.getAttribute2());
                        discardedBin.add(binTemp);
                        dominates.put(r, discardedBin);
                    } else {
                        dominates.get(r).add(brTemp);
//                        List<Bin> temp = dominates.get(r);
//                        temp.add(brTemp);
//                        dominates.put(r, temp);
                    }
                    r = r + 1;
                    brTemp = new Bin(binSize); //generate a new bin
                } else {
                    while (true) {
                        // L = L U Br;
                        for (Item item : brTemp.items) {
                            int index = itemsIndex.get(item);
                            itemAssignedFlag[index] = false;
                        }
                        itemNotAssignedSize += brTemp.items.size();

                        boolean mbIsEmpty = getBrFromMB(brTemp);
                        if (mbIsEmpty && r > 1) {
                            if (!dominates.containsKey(r)) {
                                ArrayList<Bin> discardedBin = new ArrayList<>();
                                dominates.put(r, discardedBin);
                            } else {
                                dominates.get(r).clear();
                            }
//                            dominates.get(r).clear();
                            brTemp = binsTemp.get(binsTemp.size() - 1);
                            binsTemp.remove(brTemp);
                            r = r - 1;
                            continue;
                        }
                        if (mbIsEmpty && r == 1) {
                            optimalBins.clear();
                            optimalBins.addAll(currentBestBins);
                            System.out.println("跳出1：遍历完毕");
                            return;
                        }
                        if (!mbIsEmpty) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean getBrFromMB(Bin bin) {
        boolean mbIsEmpty = false;
        boolean getBr = false;
        if (bin.items.isEmpty()) {//产生第一个maximal bin
            for (int i = 0; i < items.size(); i++) {
                if (!itemAssignedFlag[i]) {
                    Item item = items.get(i);
                    boolean putItem = bin.put(item);
                    if (putItem) {
                        getBr = true;
                    }
                }
            }
            if (getBr) {
                mbIsEmpty = false;
            } else {
                mbIsEmpty = true;
            }
        } else {
            while (!getBr) {
                Item lastItem = bin.removeLastItem();
                if (bin.items.size() == 0) {
                    mbIsEmpty = true;
                    break;
                }

                int index = itemsIndex.get(lastItem);
                for (int i = index + 1; i < items.size(); i++) {
                    Item item = items.get(i);
                    if (item.getAttribute1() == lastItem.getAttribute1() && item.getAttribute2() == lastItem.getAttribute2()) {
                        continue;
                    }
                    if (itemAssignedFlag[i]) {
                        continue;// has assigned;
                    }
                    boolean putItem = bin.put(item);
                    if (putItem) {
                        getBr = true;
                    }
                }
                if (getBr) {
                    mbIsEmpty = false;
                }
            }
        }
        return mbIsEmpty;
    }

    private void initialization() {
        dominates.clear();
        r = 1;
        k = currentSol - 1;
        binsTemp.clear();
        Arrays.fill(itemAssignedFlag, false);
        itemNotAssignedSize = n;
    }

    // Step 4 : check whether to terminate the procedure;
    private boolean termination() {
        // true : if r == lb, r is optimal, stop;
        // false: set currentSol = r;
        if (r == lb) {
            return true;
        } else {
            currentSol = r;
            return false;
        }
    }

    private boolean dominanceTest(Bin brTemp) {
        if (!dominates.containsKey(r)) {
            return true;
        }
        List<Bin> dor = dominates.get(r);
        for (Bin bin : dor) {
            if (dominate(bin, brTemp)) {
                return false;
            }
        }
        return true;
    }

    private boolean dominate(Bin bin1, Bin bin2) {
        for (Item item1 : bin1.items) {
            boolean flag = false;
            for (Item item2 : bin2.items) {
                if (item1.getAttribute1() < item2.getAttribute1() || item1.getAttribute2() < item2.getAttribute2()) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return true;
            }
        }
        return false;
    }

    private Item getMaxItem(Bin bin, int index) {
        int m = 0;
        Item res = null;
        if (index == 1) {
            for (Item item : bin.items) {
                if (item.getAttribute1() > m) {
                    res = item;
                    m = item.getAttribute1();
                }
            }
        } else {
            for (Item item : bin.items) {
                if (item.getAttribute2() > m) {
                    res = item;
                    m = item.getAttribute1();
                }
            }
        }
        return res;
    }

    private boolean lowerBoundTest(List<Item> itemList) {
        int currentLb = getLb(itemList);
        if ((currentLb + r) <= k) {
//            System.out.println("下界检测成功！！");
            return true;
        } else {
//            System.out.println("下界检测失败");
            return false;
        }
    }

    private int getLb(List<Item> itemList) {
        return Math.max(getLb1(itemList), getLb2(itemList));
    }

    private int getLb1(List<Item> itemList) {
        int sum1 = 0;
        int sum2 = 0;
        for (int i = 0; i < itemList.size(); i++) {
            sum1 += itemList.get(i).getAttribute1();
            sum2 += itemList.get(i).getAttribute2();
        }
        int bin1 = (int) Math.ceil((double) sum1 / binSize.getAttribute1());
        int bin2 = (int) Math.ceil((double) sum2 / binSize.getAttribute2());
        return Math.max(bin1, bin2);

    }

    public int getLb2(List<Item> itemList) {
        int l = 0;
        PriorityQueue<Item> v = new PriorityQueue<>(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o2.getAttribute1() - o1.getAttribute1();
            }
        });
        PriorityQueue<Item> s = new PriorityQueue<>(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o2.getAttribute2() - o1.getAttribute2();
            }
        });
        v.addAll(itemList);
        int size1 = binSize.getAttribute1();
        int size2 = binSize.getAttribute2();
        while (!v.isEmpty()) {
            l = l + 1;
            Item i = v.poll();
            s.clear();
            Set<Item> Ni = new HashSet<>();
            Set<Item> Nj = new HashSet<>();
            for (Item item : v) {
                if (item.getAttribute1() + i.getAttribute1() > size1 || item.getAttribute2() + i
                        .getAttribute2() > size2) {
                    Ni.add(item);
                } else {
                    s.add(item);
                }
            }
            Item j = s.poll();
            if (j != null) {
                for (Item item : v) {
                    if (item != j) {
                        if (item.getAttribute1() + j.getAttribute1() > size1 || item.getAttribute2() + j
                                .getAttribute2() > size2) {
                            Nj.add(item);
                        }
                    }
                }
            }
            v.clear();
            Ni.addAll(Nj);
            v.addAll(Ni);
        }
        return l;
    }

    public void printBestBins() {
        System.out.println("Algorithm: branch and bound; Bins(size:" + optimalBins.size() + "):");

        for (Bin bin : optimalBins) {
            System.out.println(bin.toString());
        }
        System.out.println();

    }
}
