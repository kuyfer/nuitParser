package com.ram.nuitparser.model.core;

import com.ram.nuitparser.model.AircraftInfo;
import com.ram.nuitparser.model.RejectInfo;
import com.ram.nuitparser.model.SenderInfo;
import com.ram.nuitparser.enums.TelexType;
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
