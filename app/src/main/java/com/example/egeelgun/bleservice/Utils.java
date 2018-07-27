package com.example.egeelgun.bleservice;

import java.util.BitSet;

public class Utils {

    //Converts int to BitSet.
    public static BitSet convert(int x) {
        BitSet bits = new BitSet();
        int index = 0;
        while (x != 0) {
            if (x % 2 == 0) {
                bits.set(index);
            }
            index++;
            x = x >>> 1;
        }
        return bits;
    }
}
