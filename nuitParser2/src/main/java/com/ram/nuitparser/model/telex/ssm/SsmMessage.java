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

    // Getters and Setters
    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getFlightDesignator() {
        return flightDesignator;
    }


    @Override
    public String getDepartureAirport() {
        return departureAirport;
    }


    @Override
    public String getArrivalAirport() {
        return arrivalAirport;
    }


    // Enrichment methods implementation
    @Override
    public void setAirlineName(String name) {
        this.airlineName = name;
    }


    @Override
    public void setAirlineCountry(String country) {
        this.airlineCountry = country;
    }

    @Override
    public void setDepartureAirportName(String name) {
        this.departureAirportName = name;
    }

    @Override
    public void setDepartureTimezone(String timezone) {
        this.departureTimezone = timezone;
    }


    @Override
    public void setArrivalAirportName(String name) {
        this.arrivalAirportName = name;
    }


    @Override
    public void setArrivalTimezone(String timezone) {
        this.arrivalTimezone = timezone;
    }

}