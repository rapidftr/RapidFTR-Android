package com.rapidftr.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class HighlightInfo{

    private String order;
    private Boolean highlighted;

}
