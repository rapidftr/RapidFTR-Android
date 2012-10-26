package com.rapidftr.forms;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
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
    private List<String> optionStrings;

    private Object value;

}
