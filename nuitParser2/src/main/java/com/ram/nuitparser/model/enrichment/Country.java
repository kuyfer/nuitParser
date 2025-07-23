package com.ram.nuitparser.model.enrichment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {
    @JsonProperty("name")
    private String name;

    @JsonProperty("iso_code")
    private String iso_code;
}