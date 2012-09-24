package com.rapidftr.service;

import com.rapidftr.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

public class FormService extends Service {

    public HttpResponse getPublishedFormSections(String url) throws IOException {
        return httpClient.get(url);
    }



}
