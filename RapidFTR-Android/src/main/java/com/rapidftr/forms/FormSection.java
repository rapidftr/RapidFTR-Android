package com.rapidftr.forms;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class FormSection implements Comparable<FormSection> {
    @JsonProperty("name")
    private HashMap<String, String> name;

    public String getName(){
        return name != null ? name.get(Locale.getDefault().getLanguage()) : null;
    }

    private int order;

    private boolean enabled;

    @JsonProperty("help_text")
    private HashMap<String, String> helpText;

    public String getHelpText(){
        return helpText != null ? helpText.get(Locale.getDefault().getLanguage()) : null;
    }


    private List<FormField> fields = new ArrayList<FormField>();

    @Override
    public int compareTo(FormSection other) {
        int otherOrder = other == null ? Integer.MIN_VALUE : other.getOrder();
        return Integer.valueOf(this.order).compareTo(otherOrder);
    }

    public String toString() {
        return name.get(Locale.getDefault().getLanguage());
    }

}
