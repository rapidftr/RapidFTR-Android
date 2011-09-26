package com.rapidftr.service;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class AbstractService {

    protected static HttpClient httpClient = new DefaultHttpClient();

}
