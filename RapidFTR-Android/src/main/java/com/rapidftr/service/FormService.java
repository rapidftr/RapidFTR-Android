package com.rapidftr.service;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
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
        HttpResponse formSectionsResponse = http()
                .path("/published_form_sections")
                .context(context)
                .get();

        String formSectionsTemplate = CharStreams.toString(new InputStreamReader(formSectionsResponse.getEntity().getContent()));
        context.setFormSectionsTemplate(formSectionsTemplate);
    }

}
