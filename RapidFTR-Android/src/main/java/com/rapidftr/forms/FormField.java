package com.rapidftr.forms;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class FormField {

    @JsonProperty("name")
    private String id;

    @JsonProperty("highlight_info")
    private HighlightInfo highlightInfo;

    private boolean editable;

    private String type;

    @JsonProperty("display_name")
    private HashMap<String, String> displayName;

    public String getDisplayName(){
        return displayName != null ? displayName.get(Locale.getDefault().getLanguage()) : null;
    }

    @JsonProperty("help_text")
    private HashMap<String, String> helpText;
    public String getHelpText(){
        return helpText != null ? helpText.get(Locale.getDefault().getLanguage()) : null;
    }


    @JsonProperty("option_strings_text")
    private HashMap<String, List<String>> optionStrings = new HashMap<String, List<String>>();

    public List<String> getOptionStrings(){
        return optionStrings != null ? optionStrings.get(Locale.getDefault().getLanguage()) : null;
    }


    private Object value;

}
