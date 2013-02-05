package com.rapidftr.forms;

import com.rapidftr.RapidFtrApplication;
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
        if(valueMap != null)
            return valueMap.get(Locale.getDefault().getLanguage()) != null ? valueMap.get(Locale.getDefault().getLanguage()) : valueMap.get(RapidFtrApplication.getDefaultLocale());
        return null;
    }


}
