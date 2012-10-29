package com.rapidftr.forms;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class FormSection implements Comparable<FormSection> {

    private String name;

    private int order;

    private boolean enabled;

    @JsonProperty("help_text")
    private String helpText;

    private List<FormField> fields = new ArrayList<FormField>();

    @Override
    public int compareTo(FormSection other) {
        int otherOrder = other == null ? Integer.MIN_VALUE : ((FormSection)other).getOrder();
        return Integer.valueOf(this.order).compareTo(otherOrder);
    }

    public String toString() {
        return name;
    }

}
