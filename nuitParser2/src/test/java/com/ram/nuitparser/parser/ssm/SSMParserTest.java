package com.ram.nuitparser.parser.ssm;

import com.ram.nuitparser.model.telex.ssm.SsmMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SSMParserTest {

    private SSMParser parser;

    @BeforeEach
    void setUp() {
        parser = new SSMParser();
    }

    @Test
    void testParseSSMMessage() {
        String rawMessage = String.join("\n",
                "SSM",
                "UTC",
                "04AUG35543E001/P37705/06784/MCHAMI",
                "RPL",
                "AT248",
                "04SEP25 11SEP25 1234",
                "J 332 J24Y275 3/HFM 4/HFM 5/AT",
                "CMN0140 MED0740",
                "MED0910 JED1010"
        );

        // Common header fields
        String priority = "QN";
        String destination = "STX,CASPCAT";
        String origin = "CASPCAT";
        String msgId = "041348";
        String header = "=PRIORITY QN =DESTINATION TYPE B STX,CASPCAT =ORIGIN CASPCAT =MSGID 041348";
        String dblSig = "d";
        String smi= "d";

        SsmMessage message = parser.parse(rawMessage, priority, destination, origin, msgId, header, dblSig, smi);

        // 🔹 Assert header fields
        assertEquals("QN", message.getPriority(), "Priority mismatch");
        assertEquals("STX,CASPCAT", message.getDestination(), "Destination mismatch");
        assertEquals("CASPCAT", message.getOrigin(), "Origin mismatch");
        assertEquals("041348", message.getMsgId(), "MsgId mismatch");
       // assertEquals(header, message.getHeader(), "Header mismatch");
        //assertEquals(rawMessage, message.getBody(), "Body mismatch");

        // 🔹 Assert type
        assertEquals("SSM", message.getType());

        // 🔹 Assert flight designator
        assertEquals("AT248", message.getFlightDesignator());

        // 🔹 Assert aircraft type
        assertEquals("332", message.getAircraftType());

        // 🔹 Assert effective & discontinuation dates
        assertEquals("04SEP25", message.getEffectiveDate());
        assertEquals("11SEP25", message.getDiscontinuationDate());

        // 🔹 Assert days of operation
        assertEquals("1234", message.getDaysOfOperation());

        // 🔹 Assert route & times
        assertEquals("CMN", message.getDepartureAirport());
        assertEquals("0140", message.getDepartureTime());
        assertEquals("MED", message.getArrivalAirport());
        assertEquals("0740", message.getArrivalTime());

        // 🔹 Assert route string (if parser fills it)
        assertNotNull(message.getRoute(), "Route should not be null");

        // 🔹 Assert remarks (if parser fills it)
        assertNotNull(message.getRemarks(), "Remarks should not be null or empty");
    }
}
