package com.ram.nuitparser.model.telex.ssm;

import com.ram.nuitparser.model.telex.TelexMessage;
import lombok.Data;

@Data
public class SsmMessage implements TelexMessage {
    private String type = "SSM";
    private String sender;
    private String receivers;
    private String flightDesignator;
    private String departureAirport;
    private String arrivalAirport;
    private String aircraftType;
    private String effectiveDate;
    private String discontinuationDate;
    private String daysOfOperation;
    private String departureTime;
    private String arrivalTime;
    private String route;
    private String remarks;

    // Enrichment fields
    private String airlineName;
    private String airlineCountry;
    private String departureAirportName;
    private String departureTimezone;
    private String arrivalAirportName;
    private String arrivalTimezone;
}