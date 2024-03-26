package org.currency.bean;

import java.util.HashMap;

public class Steps {
    private static final HashMap<String, String> steps = new HashMap<>();

    public static String get(String key) {
        return steps.getOrDefault(key,"main");
    }

    public static void set(String key, String step) {
        steps.put(key,step);
    }

}
