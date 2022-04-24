package xyz.srcbox.lin.ui.util;

import java.util.HashMap;

public class Bv2av {

    //https://blog.csdn.net/ty1937/article/details/105080093

    private static String table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    private static HashMap<String, Integer> mp = new HashMap<>();
    private static HashMap<Integer, String> mp2 = new HashMap<>();
    static int ss[] = {11, 10, 3, 8, 4, 6, 2, 9, 5, 7};
    static long xor = 177451812;
    static long add = 8728348608l;

    public  String b2v(String s) {
        long r = 0;
        for (int i = 0; i < 58; i++) {
            String s1 = table.substring(i, i + 1);
            mp.put(s1, i);
        }
        for (int i = 0; i < 6; i++) {
            r = r + mp.get(s.substring(ss[i], ss[i] + 1)) * power(58, i);
        }
        return "av" + ((r - add) ^ xor);
    }

    public static long power(int a, int b) {
        long power = 1;
        for (int c = 0; c < b; c++)
            power *= a;
        return power;
    }
}
