package com.rapidftr.forms;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormSection implements Comparable<FormSection> {

    private String name;

    private int order;

    private boolean enabled;

    @JsonProperty("help_text")
    private String helpText;

    private List<FormField> fields = new ArrayList<FormField>();

    @Override
    public int compareTo(FormSection other) {
        int otherOrder = other == null ? Integer.MIN_VALUE : ((FormSection)other).getOrder();
        return Integer.valueOf(this.order).compareTo(otherOrder);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }
}
