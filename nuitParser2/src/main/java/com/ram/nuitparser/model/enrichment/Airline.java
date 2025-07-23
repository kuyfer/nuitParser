package com.ram.nuitparser.model.enrichment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Airline {

    @JsonProperty("Airline ID")
    private String airlineId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Alias")
    private String alias;

    @JsonProperty("IATA")
    private String iata;

    @JsonProperty("ICAO")
    private String icao;

    @JsonProperty("Callsign")
    private String callsign;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Active")
    private String active;

    // Getters and setters (or use Lombok)
}