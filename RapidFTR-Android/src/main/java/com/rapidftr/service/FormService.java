package com.rapidftr.service;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.rapidftr.utils.http.FluentRequest.http;

public class FormService {

    private RapidFtrApplication context;

    @Inject
    public FormService(RapidFtrApplication context) {
        this.context = context;
    }

    public void getPublishedFormSections() throws IOException {
        FluentResponse formSectionsResponse = http()
                .context(context)
		        .path("/published_form_sections")
                .get();

        if (formSectionsResponse.isSuccess()) {
            String formSectionJson = CharStreams.toString(new InputStreamReader(formSectionsResponse.getEntity().getContent()));
            context.setFormSections(formSectionJson);
        }
    }

}