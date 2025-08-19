package com.ram.nuitparser.model.telex.mvt;

import com.ram.nuitparser.model.telex.TelexMessage;
import lombok.Data;

@Data
public class MvtMessage implements TelexMessage {
    private String type = "MVT";
    private String sender;
    private String receivers;
    private String flightDesignator;
    private String departureAirport;
    private String arrivalAirport;
    private String aircraftRegistration;
    private String aircraftType;
    private String departureTime;
    private String arrivalTime;
    private String status;
    private String runway;
    private String gate;
    private String baggageCarousel;
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