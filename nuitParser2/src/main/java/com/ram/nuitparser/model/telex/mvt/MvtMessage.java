package com.ram.nuitparser.model.telex.mvt;

import com.ram.nuitparser.model.telex.TelexMessage;
import lombok.Data;

@Data
public class MvtMessage implements TelexMessage {
    private String type = "MVT";
    private String flightDesignator;
    private String departureAirport;
    private String arrivalAirport;
    private String aircraftRegistration;
    private String aircraftType;
    private String dateOfFlight;
    private String rawBody;

    // Header fields (common across all telex types)
    private String priority;
    private String destination;
    private String origin;
    private String msgId;

    // Movement times
    private String actualOffBlockTime;
    private String actualTakeoffTime;
    private String actualLandingTime;
    private String actualInBlockTime;

    // Status information
    private String movementStatus; // DEPARTED, ARRIVED, etc.
    private String delayReason;
    private String remarks;

    // Enrichment fields
    private String airlineName;
    private String airlineCountry;
    private String departureAirportName;
    private String departureTimezone;
    private String arrivalAirportName;
    private String arrivalTimezone;
}