package com.rapidftr.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Radu Muresan
 * Date: 9/26/11
 * Time: 2:56 PM
 */
public class FormService extends AbstractService{

    public HttpResponse getPublishedFormSections(String url) throws IOException {
        HttpGet get = new HttpGet("http://" + url + "/published_form_sections");
        get.addHeader("Accept", "application/json");
        return httpClient.execute(get);
    }

}
