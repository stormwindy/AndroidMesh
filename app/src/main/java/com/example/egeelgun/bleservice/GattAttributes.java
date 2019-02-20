package com.example.egeelgun.bleservice;

import java.util.HashMap;

public class GattAttributes {
    public static HashMap<String, String> bAttribute = new HashMap<>();
    public static String heartRate = "1100-0011";
    static {
        bAttribute.put(heartRate, "HEART_RATE_MEASUREMENT");
        bAttribute.put("010101011", "uuid");
    }
    //TODO: Create the packet structure.
    public static String search(String uuid, String defaultwham) {
        String name = bAttribute.get(uuid);
        if(name == null) {
            return defaultwham;
        }
        return name;
    }
}
