package com.rapidftr.forms;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormField {

    @JsonProperty("id")
    private String id;

    private boolean enabled;

    @JsonProperty("highlight_info")
    private HighlightInfo highlightInfo;

    private boolean editable;

    private String type;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("help_text")
    private String helpText;

    @JsonProperty("option_strings")
    private List<String> optionStrings;

    private Object value;

    public Object getValue(){
        return value;
    }

    public void setValue(Object value){
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HighlightInfo getHighlightInfo() {
        return highlightInfo;
    }

    public void setHighlightInfo(HighlightInfo highlightInfo) {
        this.highlightInfo = highlightInfo;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public List<String> getOptionStrings() {
        return optionStrings;
    }

    public void setOptionStrings(List<String> optionStrings) {
        this.optionStrings = optionStrings;
    }
}
