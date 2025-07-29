package com.ram.nuitparser.parser.asm;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ASMParser {
    private static final Pattern ACTION_PATTERN = Pattern.compile("^(NEW|CNL|RPL|ADM|CON|EQT|FLT|RRT|TIM)\\b");
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("\\b([A-Z]{2}\\d{3,4})/(\\d{1,2}[A-Z]{3}\\d{2})\\b");
    private static final Pattern DEI_PATTERN = Pattern.compile("\\b(\\d+/[A-Z0-9]+)\\b");
    private static final Pattern AIRCRAFT_PATTERN = Pattern.compile("([A-Z]\\w{2,3})\\s*\\.([A-Z]\\d+)");
    private static final Pattern AIRPORT_PATTERN = Pattern.compile("([A-Z]{3})(\\d{6})");

    public AsmMessage parse(String body, String sender, String receivers) {
        AsmMessage message = new AsmMessage();
        message.setSender(sender);
        message.setReceivers(receivers);
        message.setRawBody(body);

        List<String> deiList = new ArrayList<>();
        List<String> airports = new ArrayList<>();

        for (String line : body.split("\\n")) {
            line = line.trim();

            // Parse action
            Matcher actionMatcher = ACTION_PATTERN.matcher(line);
            if (actionMatcher.find()) {
                message.setAction(actionMatcher.group(1));
            }

            // Parse flight info
            Matcher flightMatcher = FLIGHT_PATTERN.matcher(line);
            if (flightMatcher.find()) {
                message.setFlightDesignator(flightMatcher.group(1));
                message.setFlightDate(flightMatcher.group(2));
            }

            // Parse DEIs
            Matcher deiMatcher = DEI_PATTERN.matcher(line);
            while (deiMatcher.find()) {
                deiList.add(deiMatcher.group(1));
            }

            // Parse aircraft
            Matcher acMatcher = AIRCRAFT_PATTERN.matcher(line);
            if (acMatcher.find()) {
                message.setAircraftType(acMatcher.group(1));
                message.setEquipmentVersion(acMatcher.group(2));
            }

            // Parse airports
            Matcher aptMatcher = AIRPORT_PATTERN.matcher(line);
            while (aptMatcher.find()) {
                airports.add(aptMatcher.group(1));
                if (aptMatcher.group(2) != null) {
                    if (message.getDepartureTime() == null) {
                        message.setDepartureTime(aptMatcher.group(2));
                    } else {
                        message.setArrivalTime(aptMatcher.group(2));
                    }
                }
            }
        }

        if (airports.size() >= 2) {
            message.setDepartureAirport(airports.get(0));
            message.setArrivalAirport(airports.get(1));
        }

        message.setDeIdentifiers(deiList);
        System.out.println(message);
        return message;
    }
}