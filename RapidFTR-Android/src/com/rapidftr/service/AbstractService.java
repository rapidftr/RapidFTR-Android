package com.rapidftr.service;

import com.rapidftr.Config;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class AbstractService {

    protected static HttpClient httpClient = new DefaultHttpClient();

    protected String getBaseUrl(){
        if (!Config.getBaseUrl().startsWith("http://"))
            return "http://" + Config.getBaseUrl();
        return Config.getBaseUrl();
    }

}
