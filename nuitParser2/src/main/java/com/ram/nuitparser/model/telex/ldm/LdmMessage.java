package com.ram.nuitparser.model.telex.ldm;

import com.ram.nuitparser.model.telex.TelexMessage;
import lombok.Data;

@Data
public class LdmMessage implements TelexMessage {
    private String type = "LDM";
    private String sender;
    private String receivers;
    private String flightDesignator;
    private String departureAirport;
    private String arrivalAirport;
    private String aircraftRegistration;
    private String aircraftType;
    private String dateOfFlight;
    private int passengerCount;
    private int passengerCapacity;
    private double cargoWeight;
    private double mailWeight;
    private double totalWeight;
    private String weightDistribution;
    private String specialLoads;

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
