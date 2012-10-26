package com.rapidftr.forms;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
public class HighlightInfo{

    private String order;
    private Boolean highlighted;

}
