package com.rapidftr.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

public class FormService extends AbstractService{

    public HttpResponse getPublishedFormSections(String url) throws IOException {
        HttpGet get = new HttpGet(getFormattedUrl(url) + "/published_form_sections");
        get.addHeader("Accept", "application/json");
        return httpClient.execute(get);
    }

}
