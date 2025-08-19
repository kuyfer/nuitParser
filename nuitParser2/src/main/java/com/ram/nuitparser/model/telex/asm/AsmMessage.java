package com.ram.nuitparser.model.telex.asm;

import com.ram.nuitparser.model.telex.TelexMessage;
import lombok.Data;
import java.util.List;

@Data
public class AsmMessage implements TelexMessage {
    private String type = "ASM";
    private String sender;
    private String receivers;
    private String action;
    private String flightDesignator;
    private String flightDate;
    private List<String> deIdentifiers;
    private String aircraftType;
    private String equipmentVersion;
    private String departureAirport;
    private String departureTime;
    private String arrivalAirport;
    private String arrivalTime;
    private String rawBody;

    // Enrichment fields
    private String airlineName;
    private String airlineCountry;
    private String departureAirportName;
    private String departureTimezone;
    private String arrivalAirportName;
    private String arrivalTimezone;
}