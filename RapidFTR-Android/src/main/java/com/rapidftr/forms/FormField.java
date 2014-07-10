package com.rapidftr.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapidftr.RapidFtrApplication;
import lombok.*;

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

    @JsonProperty("highlight_information")
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
        return getLocalized(displayName);
    }

    public String getLocalizedHelpText(){
        return getLocalized(helpText);
    }

    public List<String> getLocalizedOptionStrings(){
        String locale = Locale.getDefault().getLanguage();
        List<String> localizedOptionStrings = (optionStrings != null && optionStrings.get(locale) != null) ? optionStrings.get(locale) : null;
        if(localizedOptionStrings == null)
            return (optionStrings != null && optionStrings.get(RapidFtrApplication.getDefaultLocale()) != null) ? optionStrings.get(RapidFtrApplication.getDefaultLocale()) : new ArrayList<String>();
        return localizedOptionStrings;
    }

    private String getLocalized(Map<String, String> valueMap) {
        if(valueMap != null) {
            String value = valueMap.get(Locale.getDefault().getLanguage());
            return (value == null || "".equals(value)) ? valueMap.get(RapidFtrApplication.getDefaultLocale()) : value;
        }
        return null;
    }


}
