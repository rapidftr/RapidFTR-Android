package com.rapidftr.forms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
public class FormSection implements Comparable{

    private String name;

    private int order;

    private boolean enabled;

    @JsonProperty("help_text")
    private String helpText;

    private List<FormField> fields = new ArrayList<FormField>();

    @Override
    public int compareTo(Object other) {
        if (!(other instanceof FormSection)) {
            throw new ClassCastException("Invalid object");
        }

        int order = ((FormSection)other).getOrder();

        if(this.getOrder() > order)
            return 1;
        else if ( this.getOrder() < order)
            return -1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
