package liuhang;

import java.util.*;

/**
 * Created by liuhang on 2017/6/12.
 */
public class BranchBound {

    private List<Bin> optimalBins = new ArrayList<>();// the bins of optimal solution
    private List<Item> items;// list of input items
    public Item binSize;//
    private int lb; // lower bound;

    private Map<Item, Integer> itemsIndex = new HashMap<>();

    private int currentSol; // value of current best solution
    private int k; // k = currentSol-1 ;denotes the value of the current solution minus one
    //    private List<Item> itemsNotAssigned = new ArrayList<>(); //list of items not yet assigned;
    private Map<Item, Integer> itemsNotAssigned = new HashMap<>();//list of items not yet assigned;
    private Map<Integer, List<Bin>> dominates = new HashMap<>();// set of discarded bins at level r（存疑）
    private int r; // current level (r already generated maximal bins present))
    private List<Bin> currentBestBins = new ArrayList<>();// list of bins of current solution
    private List<Bin> binsTemp = new ArrayList<>();// temp list of bin when the procedure is running


    public BranchBound(List<Item> in, Item binSize) {
        this.items = in;
        this.binSize = binSize;
        currentSol = items.size();
    }


    public void executeBranchBound() {
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o2.getAttribute1() + o2.getAttribute2() - o1.getAttribute1() - o1.getAttribute2();
            }
        });
        for (int i = 0; i < items.size(); i++) {
            itemsIndex.put(items.get(i), i);
        }
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
//                itemsNotAssigned.removeAll(brTemp.items);
                for (Item item : brTemp.items) {
                    itemsNotAssigned.remove(item);
                }


                if (itemsNotAssigned.isEmpty()) {
                    binsTemp.add(brTemp);
//                    r = r + 1;
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
                itemList.addAll(itemsNotAssigned.keySet());
                if (dominanceTest() && lowerBoundTest(itemList)) {//step 2
                    binsTemp.add(brTemp);

                    // update dominates
                    if (!dominates.containsKey(r)) {
                        ArrayList<Bin> discardedBin = new ArrayList<>();
                        discardedBin.add(brTemp);
                        dominates.put(r, discardedBin);
                    } else {
                        List<Bin> temp = dominates.get(r);
                        temp.add(brTemp);
                        dominates.put(r, temp);
                    }
                    r = r + 1;
                    brTemp = new Bin(binSize); //generate a new bin
                } else {
                    while (true) {
                        // L = L U Br;
//                        itemsNotAssigned.addAll(0, brTemp.items);
                        for (Item item : brTemp.items) {
                            itemsNotAssigned.put(item, itemsIndex.get(item));
                        }

                        boolean mbIsEmpty = getBrFromMB(brTemp);
                        if (mbIsEmpty && r > 1) {
//                            dominates.get(r).clear();
                            brTemp = binsTemp.get(binsTemp.size() - 1);
//                            brTemp = binsTemp.get(r - 1);
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
            Set<Integer> indexs = new HashSet<>();
            indexs.addAll(itemsNotAssigned.values());
            for (int i = 0; i < items.size(); i++) {
                if (indexs.contains(i)) {
                    Item item = items.get(i);
                    boolean putItem = bin.put(item);
                    if (putItem) {
                        getBr = true;
                    }
                }

            }
//            for (Item item : itemsNotAssigned) {
//                boolean putItem = bin.put(item);
//                if (putItem) {
//                    getBr = true;
//                }
//            }
            if (getBr) {
                mbIsEmpty = false;
            }
        } else {
            while (!getBr) {
                Item lastItem = bin.removeLastItem();
                if (bin.items.size() == 0) {
                    mbIsEmpty = true;
                    break;
                }
//                if (lastItem == null) {
//                    mbIsEmpty = true;
//                    break;
//                }
                int index = itemsIndex.get(lastItem);
                for (int i = index + 1; i < items.size(); i++) {
                    Item item = items.get(i);
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
        for (int i = 0; i < items.size(); i++) {
            itemsNotAssigned.put(items.get(i), i);
        }
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

    private boolean dominanceTest() {
        return true;
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
        System.out.println("Algorithm: branch¬ bound; Bins(size:" + optimalBins.size() + "):");

        for (Bin bin : optimalBins) {
            System.out.println(bin.toString());
        }
        System.out.println();

    }
}
