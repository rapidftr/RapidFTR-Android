package com.rapidftr.service;

import android.content.Context;
import org.apache.http.HttpResponse;

import java.io.IOException;

import static com.rapidftr.utils.FluentRequest.http;

public class FormService {

    public HttpResponse getPublishedFormSections(Context context) throws IOException {
        return http()
              .path("/published_form_sections")
              .context(context)
              .get();
    }

}
