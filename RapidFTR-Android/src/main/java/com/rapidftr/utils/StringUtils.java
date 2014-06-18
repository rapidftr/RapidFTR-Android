package com.rapidftr.utils;

public class StringUtils {

    public static Boolean isNotEmpty(String str) {
        if (str != null && str.trim().length() > 0)
            return true;

        return false;
    }
}
