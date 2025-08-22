package com.ram.nuitparser.model.telex;

public interface TelexMessage {
    String getType();
    String getFlightDesignator();
    String getDepartureAirport();
    String getArrivalAirport();

    // Header fields
    String getPriority();
    void setPriority(String priority);
    String getDestination();
    void setDestination(String destination);
    String getOrigin();
    void setOrigin(String origin);
    String getMsgId();
    void setMsgId(String msgId);
    String getHeader();
    void setHeader(String header);
    String getSender();
    void setSender(String sender);
    String getReceivers();
    void setReceivers(String receivers);

    // Enrichment methods
    void setAirlineName(String name);
    void setAirlineCountry(String country);
    void setDepartureAirportName(String name);
    void setDepartureTimezone(String timezone);
    void setArrivalAirportName(String name);
    void setArrivalTimezone(String timezone);
}