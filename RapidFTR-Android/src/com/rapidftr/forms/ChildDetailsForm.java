package com.rapidftr.forms;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildDetailsForm implements Comparable{
    private String name;
    private int order;
    private boolean enabled;
    private ArrayList<FormField> fields = new ArrayList<FormField>();

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

    public ArrayList<FormField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<FormField> fields) {
        this.fields = fields;
    }

    @Override
    public int compareTo(Object other) {
        if (!(other instanceof ChildDetailsForm)) {
            throw new ClassCastException("Invalid object");
        }

        int order = ((ChildDetailsForm)other).getOrder();

        if(this.getOrder() > order)
            return 1;
        else if ( this.getOrder() < order)
            return -1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
