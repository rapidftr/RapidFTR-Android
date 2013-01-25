package com.rapidftr.forms;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.*;

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
    private Map<String, String> displayName;

	@JsonProperty("help_text")
	private Map<String, String> helpText;

	@JsonProperty("option_strings_text")
	private Map<String, List<String>> optionStrings = new HashMap<String, List<String>>();

	private Object value;

    public String getLocalizedDisplayName(){
        return displayName != null ? displayName.get(Locale.getDefault().getLanguage()) : "";
    }

    public String getLocalizedHelpText(){
        return helpText != null ? helpText.get(Locale.getDefault().getLanguage()) : "";
    }

    public List<String> getLocalizedOptionStrings(){
        String locale = Locale.getDefault().getLanguage();
        return (optionStrings != null && optionStrings.get(locale) != null) ? optionStrings.get(locale) : new ArrayList<String>();
    }

}
