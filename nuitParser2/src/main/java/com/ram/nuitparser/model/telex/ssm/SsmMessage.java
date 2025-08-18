package com.ram.nuitparser.model.telex.ssm;

import com.ram.nuitparser.model.telex.TelexMessage;

public class SsmMessage implements TelexMessage


{
    @Override
    public String getType() {
        return "SSM";
    }

    @Override
    public String getFlightDesignator() {
        return "";
    }

    @Override
    public String getDepartureAirport() {
        return "";
    }

    @Override
    public String getArrivalAirport() {
        return "";
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
