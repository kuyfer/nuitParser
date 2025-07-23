package com.ram.nuitparser.model;

import lombok.Data;

@Data
public class FlightInfo {
    private String flightNumber;
    private String airlineCode;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private String departureTimeUTC;
    private String arrivalTimeUTC;
    private String dateOfFlight;
}

