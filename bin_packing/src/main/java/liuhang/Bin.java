package liuhang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhang on 2017/6/10.
 */
public class Bin {
    public Item maxSize;
    public Item currentSize;
    public List<Item> items;

    public Bin(Item maxSize) {
        this.maxSize = new Item(maxSize.getAttribute1(), maxSize.getAttribute2());
        this.currentSize = new Item(0, 0);
        this.items = new ArrayList<>();
    }

    public boolean put(Item item) {
        if (Item.IsEnough(item, currentSize, maxSize)) {
            items.add(item);
            currentSize.addAttribute1(item.getAttribute1());
            currentSize.addAttribute2(item.getAttribute2());
            return true;
        } else {
            return false;
        }
    }

    public Item removeLastItem() {
        if (!items.isEmpty()) {
            Item lastItem = items.get(items.size() - 1);
            int a1 = currentSize.getAttribute1();
            int a2 = currentSize.getAttribute2();
            currentSize.setAttribute1(a1 - lastItem.getAttribute1());
            currentSize.setAttribute2(a2 - lastItem.getAttribute2());
            items.remove(lastItem);
            return lastItem;
        } else {
            return null;
        }
    }

    public int numberOfItems() {
        return items.size();
    }

    @Override
    public String toString() {
        String res = "";
        for (Item item : items) {
            res += "(" + item.getAttribute1() + "," + item.getAttribute2() + "); ";
        }
        res += "    Size: (" + currentSize.getAttribute1() + "," + currentSize.getAttribute2() + ");";
        res += "    max:(" + maxSize.getAttribute1() + "," + maxSize.getAttribute2() + ")";
        res += "    residual:(" + (maxSize.getAttribute1() - currentSize.getAttribute1()) + "," + (maxSize
                .getAttribute2() - currentSize.getAttribute2()) + ")";
        return res;
    }
}
