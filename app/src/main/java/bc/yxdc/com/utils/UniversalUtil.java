package bc.yxdc.com.utils;

import java.util.Random;

/**
 * Created by gamekonglee on 2018/9/20.
 */

public class UniversalUtil {
    /**
     * 得到a到b的随机整数[a,b)
     *
     * @param a
     * @param b
     * @return int
     */
    public static int randomA2B(int a, int b) {
        int temp = new Random().nextInt(b - a);
        return (temp + a);
    }

}
