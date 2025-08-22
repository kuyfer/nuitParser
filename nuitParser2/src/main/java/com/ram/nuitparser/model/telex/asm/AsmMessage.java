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
    private String rawBody;

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

    /**
     * Returns the full description of the action based on the action code
     */
    public String getActionDescription() {
        if (action == null) {
            return "Unknown action";
        }

        try {
            AsmAction actionEnum = AsmAction.valueOf(action);
            switch (actionEnum) {
                case NEW: return "New flight leg";
                case CNL: return "Canceled flight";
                case RIN: return "Reinstate flight";
                case RPL: return "Replace flight";
                case ADM: return "Administrative change";
                case CON: return "Consolidation";
                case EQT: return "Equipment change";
                case FLT: return "Flight data change";
                case RRT: return "Route change";
                case TIM: return "Time change";
                default: return action;
            }
        } catch (IllegalArgumentException e) {
            return action; // Return the code as is if not found in enum
        }
    }

    /**
     * Returns both the code and description for display purposes
     */
    public String getActionWithDescription() {
        return action + " - " + getActionDescription();
    }
}