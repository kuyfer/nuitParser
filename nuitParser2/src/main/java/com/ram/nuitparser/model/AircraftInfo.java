package com.ram.nuitparser.model;

import lombok.Data;

@Data
public class AircraftInfo {
    private String iataCode;
    private String icaoCode;
    private String registration;
    private String version;
}
