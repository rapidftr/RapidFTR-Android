package com.rapidftr.forms;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormField{
    private String name;
    private boolean enabled;
    private HighlightInfo highlight_information;
    private boolean editable;
    private String type;
    private String display_name;
    private String help_text;
    private ArrayList<String> option_strings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HighlightInfo getHighlight_information() {
        return highlight_information;
    }

    public void setHighlight_information(HighlightInfo highlight_information) {
        this.highlight_information = highlight_information;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getHelp_text() {
        return help_text;
    }

    public void setHelp_text(String help_text) {
        this.help_text = help_text;
    }

    public ArrayList<String> getOption_strings() {
        return option_strings;
    }

    public void setOption_strings(ArrayList<String> option_strings) {
        this.option_strings = option_strings;
    }
}
