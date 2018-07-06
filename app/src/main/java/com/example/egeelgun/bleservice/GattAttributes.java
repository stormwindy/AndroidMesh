/**
 * Author: Ege Elgun.
 * Data: 06.07.2018
 * All rights reserved: ICTerra Information and Communication Technologies Inc.
 */
package com.example.egeelgun.bleservice;

import java.util.HashMap;

public class GattAttributes {
    public static HashMap<String, String> bAttribute = new HashMap<>();
    public static String heartRate = "1100-0011";
    static {
        bAttribute.put(heartRate, "HEART_RATE_MEASUREMENT");
    }
}
