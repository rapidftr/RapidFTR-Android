package com.rapidftr.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

public class FormService extends AbstractService{

    public HttpResponse getPublishedFormSections() throws IOException {
        HttpGet get = new HttpGet(getBaseUrl() + "/published_form_sections");
        get.addHeader("Accept", "application/json");
        return httpClient.execute(get);
    }

}
