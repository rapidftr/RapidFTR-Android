package com.rapidftr;

public class Config {

    private static String baseUrl;

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String url) {
        Config.baseUrl = url;
    }

}
