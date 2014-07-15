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
        if (valueMap != null) {
            String value = valueMap.get(Locale.getDefault().getLanguage());
            return (value == null || "".equals(value)) ? valueMap.get(RapidFtrApplication.getDefaultLocale()) : value;
        }
        return null;
    }

    @Override
    public int compareTo(FormSection other) {
        int otherOrder = other == null ? Integer.MIN_VALUE : other.getOrder();
        return Integer.valueOf(this.order).compareTo(otherOrder);
    }

    public String toString() {
        return getLocalizedName();
    }

    public List<FormField> getOrderedHighLightedFields() {
        SortedMap<Integer, FormField> sortedFormFields = new TreeMap<Integer, FormField>();
        for (FormField formField : fields) {
            if (formField.getHighlightInfo() != null && formField.getHighlightInfo().getHighlighted()) {
                Integer order = Integer.parseInt(formField.getHighlightInfo().getOrder());
                sortedFormFields.put(order, formField);
            }
        }

        return Arrays.asList(sortedFormFields.values().toArray(new FormField[]{}));
    }
}
