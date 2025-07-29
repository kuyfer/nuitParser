package com.ram.nuitparser.parser.asm;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ASMParser {
    // Improved regex patterns
    private static final Pattern ACTION_PATTERN = Pattern.compile("\\b(NEW|CNL|RPL|ADM|CON|EQT|FLT|RRT|TIM)\\b");
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("\\b([A-Z]{2}\\d{1,4})/(\\d{1,2}[A-Z]{3}\\d{2})\\b");
    private static final Pattern DEI_PATTERN = Pattern.compile("\\b(\\d+/[A-Z0-9]+)\\b");
    private static final Pattern AIRCRAFT_PATTERN = Pattern.compile("\\b([A-Z]\\w{2,3})\\s*\\.([A-Z]\\d+)\\b");
    private static final Pattern AIRPORT_PATTERN = Pattern.compile("\\b([A-Z]{3})(\\d{4,6})\\b");  // Supports 4-6 digit times

    public AsmMessage parse(String body, String sender, String receivers) {
        AsmMessage message = new AsmMessage();
        message.setSender(sender);
        message.setReceivers(receivers);
        message.setRawBody(body);

        List<String> deiList = new ArrayList<>();
        List<String> airports = new ArrayList<>();
        List<String> times = new ArrayList<>();

        // Process each non-empty line
        for (String line : body.split("\\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // 1. Parse action code
            extractAction(message, line);

            // 2. Parse flight designator and date
            extractFlightInfo(message, line);

            // 3. Parse DEI elements
            extractDEIs(deiList, line);

            // 4. Parse aircraft info
            extractAircraftInfo(message, line);

            // 5. Parse airports and times
            extractAirportsAndTimes(airports, times, line);
        }

        // Assign departure/arrival airports and times
        if (!airports.isEmpty()) {
            message.setDepartureAirport(airports.get(0));
            if (airports.size() > 1) {
                message.setArrivalAirport(airports.get(1));
            }
        }
        if (!times.isEmpty()) {
            message.setDepartureTime(times.get(0));
            if (times.size() > 1) {
                message.setArrivalTime(times.get(1));
            }
        }

        message.setDeIdentifiers(deiList);
        return message;
    }

    private void extractAction(AsmMessage message, String line) {
        Matcher actionMatcher = ACTION_PATTERN.matcher(line);
        if (actionMatcher.find() && message.getAction() == null) {
            message.setAction(actionMatcher.group(1));
        }
    }

    private void extractFlightInfo(AsmMessage message, String line) {
        if (message.getFlightDesignator() != null) return;

        Matcher flightMatcher = FLIGHT_PATTERN.matcher(line);
        if (flightMatcher.find()) {
            message.setFlightDesignator(flightMatcher.group(1));
            message.setFlightDate(flightMatcher.group(2));
        }
    }

    private void extractDEIs(List<String> deiList, String line) {
        Matcher deiMatcher = DEI_PATTERN.matcher(line);
        while (deiMatcher.find()) {
            deiList.add(deiMatcher.group(1));
        }
    }

    private void extractAircraftInfo(AsmMessage message, String line) {
        if (message.getAircraftType() != null) return;

        Matcher acMatcher = AIRCRAFT_PATTERN.matcher(line);
        if (acMatcher.find()) {
            message.setAircraftType(acMatcher.group(1));
            message.setEquipmentVersion(acMatcher.group(2));
        }
    }

    private void extractAirportsAndTimes(List<String> airports, List<String> times, String line) {
        Matcher aptMatcher = AIRPORT_PATTERN.matcher(line);
        while (aptMatcher.find()) {
            String airport = aptMatcher.group(1);
            String time = aptMatcher.group(2);

            if (airport != null && !airport.isEmpty()) {
                airports.add(airport);
            }
            if (time != null && !time.isEmpty()) {
                times.add(time);
            }
        }
    }
}