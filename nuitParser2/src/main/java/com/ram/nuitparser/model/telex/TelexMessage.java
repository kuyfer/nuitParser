package com.ram.nuitparser.model.telex;

public interface TelexMessage {
    // Core telex metadata
    String getType();
    String getPriority();
    void setPriority(String priority);
    String getDestination();
    void setDestination(String destination);
    String getOrigin();
    void setOrigin(String origin);
    String getMsgId();
    void setMsgId(String msgId);
    String getRawBody();
    void setRawBody(String rawBody);

    // Flight information (common across all telex types)
    String getFlightDesignator();
    String getDepartureAirport();
    String getArrivalAirport();

    // Enrichment methods (for external data enhancement)
    void setAirlineName(String name);
    void setAirlineCountry(String country);
    void setDepartureAirportName(String name);
    void setDepartureTimezone(String timezone);
    void setArrivalAirportName(String name);
    void setArrivalTimezone(String timezone);
}