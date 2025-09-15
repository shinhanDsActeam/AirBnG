package com.airbng.platform.util;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
