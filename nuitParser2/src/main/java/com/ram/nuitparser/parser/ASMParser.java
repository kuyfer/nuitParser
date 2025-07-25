package com.ram.nuitparser.parser;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.parser.TelexParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ASMParser implements TelexParser<AsmMessage> {

    @Override
    public AsmMessage parse(String body, String sender, String receivers) {
        AsmMessage asm = new AsmMessage();
        asm.setSender(sender);
        asm.setReceivers(receivers);
        asm.setRawBody(body);

        String[] lines = body.lines()
                .filter(l -> !l.isBlank())
                .toArray(String[]::new);

        // Exemple simplifié : on repère action, vol, date, DEIs, aéroport, etc.
        for (String line : lines) {
            if (line.matches("^(NEW|CNL|RPL|ADM|CON|EQT|FLT|RRT|TIM).*")) {
                asm.setAction(line.trim());
            }
            if (line.contains("/13JUL25")) {
                String[] parts = line.split(" ");
                asm.setFlightDesignator(parts[0]);
                asm.setFlightDate(parts[1]);
                // extraire DEI
                List<String> de = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    if (parts[i].contains("/")) de.add(parts[i]);
                }
                asm.setDeIdentifiers(de);
            }
            if (line.startsWith("J")) {
                asm.setAircraftType(line.split(" ")[1]);
                asm.setEquipmentVersion(line.split("\\.")[1]);
            }
            if (line.startsWith("CDG")) {
                asm.setDepartureAirport("CDG");
                asm.setDepartureTime(line.substring(3,9));
            }
            if (line.startsWith("OUD")) {
                asm.setArrivalAirport("OUD");
                asm.setArrivalTime(line.substring(3,9));
            }
        }
        System.out.println(asm);
        return asm;
    }
}
