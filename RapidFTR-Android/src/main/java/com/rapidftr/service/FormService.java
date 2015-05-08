package com.rapidftr.service;

import android.util.Log;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.Form;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.utils.ResourceLoader;
import com.rapidftr.utils.StringUtils;
import com.rapidftr.utils.http.FluentResponse;
import lombok.Cleanup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.rapidftr.utils.http.FluentRequest.http;

public class FormService {

    public static final String FORM_SECTIONS_PREF = "FORM_SECTION";
    public static final String API_FORM_SECTIONS_PATH = "/api/form_sections";

    private RapidFtrApplication context;

    private Map<String, Form> formsMap = new HashMap<String, Form>();

    @Inject
    public FormService(RapidFtrApplication context) {
        this.context = context;
        try {
            loadFormSections();
        } catch (IOException e) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, e.getMessage());
        }
    }

    public void downloadPublishedFormSections() throws IOException {
        FluentResponse formSectionsResponse = http()
                .context(context)
                .path(API_FORM_SECTIONS_PATH)
                .get();

        if (formSectionsResponse.isSuccess()) {
            String formSectionJson = CharStreams.toString(new InputStreamReader(formSectionsResponse.getEntity().getContent()));
            saveFormSections(formSectionJson);
            loadFormSections();
        }
    }

    protected void saveFormSections(String formSectionJson) throws IOException {
        context.getSharedPreferences().edit().putString(FORM_SECTIONS_PREF, formSectionJson).commit();
    }

    protected void loadFormSections() throws IOException {
        String formSections = context.getSharedPreferences().getString(FORM_SECTIONS_PREF, null);
        if (formSections == null) {
            formSections = loadDefaultFormSections();
        }

        parseFormSections(formSections);
    }

    private void parseFormSections(String formSections) throws IOException {
        if (StringUtils.isNotEmpty(formSections)) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(formSections);
            Iterator<Map.Entry<String, JsonNode>> childNodes = rootNode.fields();
            while (childNodes.hasNext()) {
                Map.Entry<String, JsonNode> entry = childNodes.next();
                Form form = new Form(entry.getKey(), new ArrayList<FormSection>(Arrays.asList(objectMapper.readValue(entry.getValue().toString(), FormSection[].class))));
                this.formsMap.put(entry.getKey(), form);
            }
        }
    }

    private String loadDefaultFormSections() throws IOException {
        return ResourceLoader.loadStringFromRawResource(context, R.raw.default_form_sections);
    }

    public List<FormSection> getFormSections(String formName) {

        if (this.formsMap.containsKey(formName)) {
            return this.formsMap.get(formName).getFormsections();
        }

        return Collections.emptyList();
    }

    public void setFormSections(List<FormSection> formSections) throws IOException {
        saveFormSections(new ObjectMapper().writeValueAsString(formSections.toArray()));
    }

    public List<FormField> getHighlightedFields(String formName) {
        List<FormField> formFields = new ArrayList<FormField>();

        List<FormSection> formSections = getFormSections(formName);
        for (FormSection formSection : formSections) {
            formFields.addAll(formSection.getOrderedHighLightedFields());
        }

        return formFields;
    }

    public List<FormField> getTitleFields(String formName) {
        List<FormField> formFields = new ArrayList<FormField>();

        List<FormSection> formSections = getFormSections(formName);
        for (FormSection formSection : formSections) {
            formFields.addAll(formSection.getOrderedTitleFields());
        }

        return formFields;
    }

}
