package com.ram.nuitparser.model.enrichment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aircraft {
    @JsonProperty("iataCode")
    private String iataCode;    // e.g., "CRV"

    @JsonProperty("icaoCode")
    private String icaoCode;

    @JsonProperty("name")
    private String name;        // e.g., "Aerospatiale Se.210 Caravelle"
}