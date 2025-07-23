package com.ram.nuitparser.model.enrichment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirportExtended {
    @JsonProperty("airportId")
    private int airportId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("iata")
    private String iata;

    @JsonProperty("icao")
    private String icao;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("altitude")
    private int altitude;

    @JsonProperty("timezone")
    private float timezone;

    @JsonProperty("dst")
    private String dst;

    @JsonProperty("tzDatabaseTimezone")
    private String tzDatabaseTimezone;

    @JsonProperty("type")
    private String type;

    @JsonProperty("source")
    private String source;
}