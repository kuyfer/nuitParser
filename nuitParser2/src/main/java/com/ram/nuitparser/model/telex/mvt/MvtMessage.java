package com.ram.nuitparser.model.telex.mvt;

import com.ram.nuitparser.enums.MovementType;
import com.ram.nuitparser.model.telex.TelexMessage;
import lombok.Data;

import java.util.List;

@Data
public class MvtMessage implements TelexMessage {
    private String id;
    private String timestamp;
    private String sender;
    private String receivers;
    private String rawBody;

    // Mandatory fields
    private MovementType movementType;
    private String flightDesignator;
    private String aircraftRegistration;
    private String departureAirport;
    private String arrivalAirport;

    // Times (all in UTC)
    private String actualOffBlockTime;
    private String actualTakeoffTime;
    private String actualLandingTime;
    private String actualInBlockTime;

    // Optional fields
    private String aircraftType;
    private String dayOfOperation;
    private String flightRules;
    private String flightType;
    private String departureStand;
    private String arrivalStand;
    private List<String> routePoints;
    private String remarks;






        private String type = "MVT";
        private String departureTime;
        private String arrivalTime;
        private String status;
        private String runway;
        private String gate;
        private String baggageCarousel;
        private String delayReason;


        // Enrichment fields
        private String airlineName;
        private String airlineCountry;
        private String departureAirportName;
        private String departureTimezone;
        private String arrivalAirportName;
        private String arrivalTimezone;

        // Lombok @Data annotation will generate getters and setters for all fields




    @Override
    public String getType() {
        return "MVT";
    }

    @Override
    public void setAirlineName(String name) {

    }

    @Override
    public void setAirlineCountry(String country) {

    }

    @Override
    public void setDepartureAirportName(String name) {

    }

    @Override
    public void setDepartureTimezone(String timezone) {

    }

    @Override
    public void setArrivalAirportName(String name) {

    }

    @Override
    public void setArrivalTimezone(String timezone) {

    }
}