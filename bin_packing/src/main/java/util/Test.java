package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuhang on 2017/6/11.
 */
public class Test {
    public static void main(String[] args) {
//        int a = 15;
//        int b = 10;
//        double c = (double) a / b;
//        double d = Math.ceil((double) a / b);
//        System.out.println(c);
//        System.out.println(d);
        Map<Integer, String> test = new HashMap<>();
        test.put(1, "20");
        test.put(1, "30");
        Integer a = new Integer(1);
        boolean b = test.containsKey(a);
        System.out.printf("ha");
    }
}
