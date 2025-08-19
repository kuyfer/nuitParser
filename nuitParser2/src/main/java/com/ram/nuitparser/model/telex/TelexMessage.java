package com.ram.nuitparser.model.telex;

public interface TelexMessage {
    String getType();
    String getFlightDesignator();
    String getDepartureAirport();
    String getArrivalAirport();

    // Enrichment methods
    void setAirlineName(String name);
    void setAirlineCountry(String country);
    void setDepartureAirportName(String name);
    void setDepartureTimezone(String timezone);
    void setArrivalAirportName(String name);
    void setArrivalTimezone(String timezone);

}