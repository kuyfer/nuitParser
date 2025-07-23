package com.ram.nuitparser.model;

import com.ram.nuitparser.model.enrichment.Aircraft;
import com.ram.nuitparser.model.enrichment.Airline;
import com.ram.nuitparser.model.enrichment.AirportExtended;
import com.ram.nuitparser.model.enums.TelexType;
import lombok.Data;
import java.util.List;

@Data
public class ParsedTelex {
    private TelexType type;
    private FlightInfo flightInfo;
    private AircraftInfo aircraftInfo;
    private SenderInfo senderInfo;
    private List<String> telexLines;
    private RejectInfo rejectInfo;
    private List<String> errors;
}
