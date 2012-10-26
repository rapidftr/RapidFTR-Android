package com.rapidftr.forms;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class HighlightInfo{

    private String order;
    private Boolean highlighted;

}
