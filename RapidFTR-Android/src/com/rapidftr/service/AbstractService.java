package com.rapidftr.service;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by IntelliJ IDEA.
 * User: Radu Muresan
 * Date: 9/26/11
 * Time: 2:56 PM
 */
public abstract class AbstractService {

    protected static HttpClient httpClient = new DefaultHttpClient();

}
