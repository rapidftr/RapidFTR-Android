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
public class FormSection implements Comparable<FormSection> {

    @JsonProperty("name")
    protected Map<String, String> name = new HashMap<String, String>();

    protected int order;

    protected boolean enabled;

    @JsonProperty("help_text")
    protected Map<String, String> helpText = new HashMap<String, String>();

	protected List<FormField> fields = new ArrayList<FormField>();

	public String getLocalizedName() {
        return getLocalized(name);
    }

    public String getLocalizedHelpText() {
        return getLocalized(helpText);
    }

    private String getLocalized(Map<String, String> valueMap) {
        if(valueMap != null)
            return valueMap.get(Locale.getDefault().getLanguage()) != null ? valueMap.get(Locale.getDefault().getLanguage()) : valueMap.get(RapidFtrApplication.getDefaultLocale());
        return null;
    }

    @Override
    public int compareTo(FormSection other) {
        int otherOrder = other == null ? Integer.MIN_VALUE : other.getOrder();
        return Integer.valueOf(this.order).compareTo(otherOrder);
    }

    public String toString() {
        return name.get(Locale.getDefault().getLanguage());
    }

}
