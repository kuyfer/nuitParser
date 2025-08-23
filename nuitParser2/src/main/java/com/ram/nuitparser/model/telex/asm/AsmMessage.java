package com.ram.nuitparser.model.telex.asm;

import com.ram.nuitparser.model.telex.TelexMessage;
import lombok.Data;
import java.util.List;

@Data
public class AsmMessage implements TelexMessage {
    private String type = "ASM";
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


    // Header fields (common across all telex types)
    private String priority;
    private String destination;
    private String origin;
    private String msgId;

    // Additional fields from ASM specification
    private String flightNumber;
    private String flightSuffix;
    private String operationalSuffix;
    private String daysOfOperation;
    private String periodOfOperation;
    private String crewInformation;
    private String mealService;
    private String specialHandling;

    // Enrichment fields
    private String airlineName;
    private String airlineCountry;
    private String departureAirportName;
    private String departureTimezone;
    private String arrivalAirportName;
    private String arrivalTimezone;

    public String getActionDescription() {
        if (action == null) {
            return "Unknown action";
        }

        try {
            AsmAction actionEnum = AsmAction.valueOf(action);
            return switch (actionEnum) {
                case NEW -> "New flight leg";
                case CNL -> "Canceled flight";
                case RIN -> "Reinstate flight";
                case RPL -> "Replace flight";
                case ADM -> "Administrative change";
                case CON -> "Consolidation";
                case EQT -> "Equipment change";
                case FLT -> "Flight data change";
                case RRT -> "Route change";
                case TIM -> "Time change";
                default -> action;
            };
        } catch (IllegalArgumentException e) {
            return action; // Return the code as is if not found in enum
        }
    }
}