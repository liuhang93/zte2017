package liuhang;

import java.util.List;

/**
 * Created by liuhang on 2017/6/10.
 */
public abstract class AbstractBinPacking {
    public List<Item> in;
    public Item binSize;

    public AbstractBinPacking(List<Item> in, Item binSize) {
        this.in = in;
        this.binSize = binSize;
    }
}
