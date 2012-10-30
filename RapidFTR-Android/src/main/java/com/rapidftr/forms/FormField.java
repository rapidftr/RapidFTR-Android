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
public class FormField {

    @JsonProperty("name")
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
    private List<String> optionStrings = new ArrayList<String>();

    private Object value;

}
