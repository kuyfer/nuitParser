package com.ram.nuitparser.model.telex.asm;

import lombok.Data;
import java.util.List;

@Data
public class AsmMessage {
    private String sender;
    private String receivers;
    private String action;          // NEW, RPL, CNL, etc.
    private String flightDesignator; // ex. AT651
    private String flightDate;       // ex. 13JUL25
    private List<String> deIdentifiers; // ex. ["3/X", "4/X", "5/AT", "9/X"]
    private String aircraftType;       // ex. J320
    private String equipmentVersion;   // ex. Y180
    private String departureAirport;   // ex. CDG
    private String departureTime;      // ex. 132330
    private String arrivalAirport;     // ex. OUD
    private String arrivalTime;        // ex. 140110
    private String rawBody;            // corps complet pour traçabilité



    @Override
    public String toString() {
        return "AsmMessage{" +
                "sender='" + sender + '\'' +
                ", receivers='" + receivers + '\'' +
                ", action='" + action + '\'' +
                ", flightDesignator='" + flightDesignator + '\'' +
                ", flightDate='" + flightDate + '\'' +
                ", deIdentifiers=" + deIdentifiers +
                ", aircraftType='" + aircraftType + '\'' +
                ", equipmentVersion='" + equipmentVersion + '\'' +
                ", departureAirport='" + departureAirport + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalAirport='" + arrivalAirport + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                '}';
    }

}
