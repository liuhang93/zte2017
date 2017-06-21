package liuhang;

/**
 * Created by liuhang on 2017/6/10.
 */
public class Item {
    private int attribute1;
    private int attribute2;

    public Item(int attribute1, int attribute2) {
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
    }

    public int getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(int attribute1) {
        this.attribute1 = attribute1;
    }

    public int getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(int attribute2) {
        this.attribute2 = attribute2;
    }

    public void addAttribute1(int add) {
        this.attribute1 += add;
    }

    public void addAttribute2(int add) {
        this.attribute2 += add;
    }

    public static boolean IsEnough(Item item1, Item item2, Item item3) {
        int a1 = item1.getAttribute1() + item2.getAttribute1();
        int a2 = item1.getAttribute2() + item2.getAttribute2();
        if (a1 <= item3.getAttribute1() && a2 <= item3.getAttribute2()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + attribute1 + "," + attribute2 + ")";
    }

}
