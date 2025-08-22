package com.ram.nuitparser.model.telex.ldm;

import com.ram.nuitparser.model.telex.TelexMessage;
import lombok.Data;

@Data
public class LdmMessage implements TelexMessage {
    private String type = "LDM";
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

    // Load details
    private int passengerCount;
    private int passengerCapacity;
    private int adultPassengers;
    private int childPassengers;
    private int infantPassengers;
    private double freightWeight;
    private double baggageWeight;
    private double mailWeight;
    private double totalWeight;
    private String weightIndex;

    // Compartment breakdown
    private String compartment1;
    private String compartment2;
    private String compartment3;
    private String compartment4;

    // Special information
    private String specialInstructions;
    private String notocStatus;
    private String daaStatus;

    // Enrichment fields
    private String airlineName;
    private String airlineCountry;
    private String departureAirportName;
    private String departureTimezone;
    private String arrivalAirportName;
    private String arrivalTimezone;
}