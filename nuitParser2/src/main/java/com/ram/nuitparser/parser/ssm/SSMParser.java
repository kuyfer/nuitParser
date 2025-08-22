package com.ram.nuitparser.parser.ssm;

import com.ram.nuitparser.model.telex.ssm.SsmMessage;
import com.ram.nuitparser.parser.TelexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SSMParser implements TelexParser<SsmMessage> {
    private static final Logger logger = LoggerFactory.getLogger(SSMParser.class);

    // Updated patterns based on your sample SSM message
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("\\b([A-Z]{2}\\d{2,4})\\b");
    private static final Pattern AIRCRAFT_PATTERN = Pattern.compile("\\b([A-Z]\\s*\\d{3})\\b");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\b(\\d{1,2}[A-Z]{3}\\d{2})\\b");
    private static final Pattern ROUTE_PATTERN = Pattern.compile("\\b([A-Z]{3}\\d{4})\\s+([A-Z]{3}\\d{4})\\b");
    private static final Pattern SECOND_ROUTE_PATTERN = Pattern.compile("\\b([A-Z]{3}\\d{4})\\s+([A-Z]{3}\\d{4})\\b");
    private static final Pattern CONFIG_PATTERN = Pattern.compile("\\b([A-Z]\\d+Y\\d+)\\b");

    @Override
    public SsmMessage parse(String body, String priority, String destination, String origin, String msgId, String header, String dblSig, String smi) {
        logger.info("Starting SSM message parsing");
        SsmMessage message = new SsmMessage();

        // Set common header fields
        message.setPriority(priority);
        message.setDestination(destination);
        message.setOrigin(origin);
        message.setMsgId(msgId);
       // message.setRawBody(body);

        String[] lines = body.split("\\n");
        logger.debug("Processing {} lines in SSM message", lines.length);

        // Log each line for debugging
        for (int i = 0; i < lines.length; i++) {
            logger.debug("Line {}: {}", i, lines[i].trim());
        }

        // Process each line with all patterns
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            logger.debug("Processing line {}: {}", i, line);

            // Try all patterns on each line
            extractFlightInfo(message, line, i);
            extractAircraftInfo(message, line, i);
            extractDates(message, line, i);
            extractRouteInfo(message, line, i);
            extractConfigInfo(message, line, i);
        }

        logger.info("Completed SSM message parsing for flight: {}", message.getFlightDesignator());
        logger.info("Parsed details - Departure: {}, Arrival: {}, Aircraft: {}, Effective: {}, Until: {}",
                message.getDepartureAirport(), message.getArrivalAirport(),
                message.getAircraftType(), message.getEffectiveDate(), message.getDiscontinuationDate());

        return message;
    }

    private void extractFlightInfo(SsmMessage message, String line, int lineNumber) {
        if (message.getFlightDesignator() != null) return;

        Matcher flightMatcher = FLIGHT_PATTERN.matcher(line);
        if (flightMatcher.find()) {
            message.setFlightDesignator(flightMatcher.group(1));
            logger.debug("Found flight designator on line {}: {}", lineNumber, flightMatcher.group(1));
        }
    }

    private void extractAircraftInfo(SsmMessage message, String line, int lineNumber) {
        if (message.getAircraftType() != null) return;

        Matcher acMatcher = AIRCRAFT_PATTERN.matcher(line);
        if (acMatcher.find()) {
            message.setAircraftType(acMatcher.group(1).replace(" ", ""));
            logger.debug("Found aircraft type on line {}: {}", lineNumber, acMatcher.group(1));
        }
    }

    private void extractDates(SsmMessage message, String line, int lineNumber) {
        Matcher dateMatcher = DATE_PATTERN.matcher(line);
        int dateCount = 0;

        while (dateMatcher.find()) {
            if (dateCount == 0 && message.getEffectiveDate() == null) {
                message.setEffectiveDate(dateMatcher.group(1));
                logger.debug("Found effective date on line {}: {}", lineNumber, dateMatcher.group(1));
            } else if (dateCount == 1 && message.getDiscontinuationDate() == null) {
                message.setDiscontinuationDate(dateMatcher.group(1));
                logger.debug("Found discontinuation date on line {}: {}", lineNumber, dateMatcher.group(1));
            }
            dateCount++;
        }
    }

    private void extractRouteInfo(SsmMessage message, String line, int lineNumber) {
        // Try first route pattern
        Matcher routeMatcher = ROUTE_PATTERN.matcher(line);
        if (routeMatcher.find()) {
            String dep = routeMatcher.group(1).substring(0, 3); // Extract airport code from "CMN0140"
            String arr = routeMatcher.group(2).substring(0, 3); // Extract airport code from "MED0740"

            if (message.getDepartureAirport() == null) {
                message.setDepartureAirport(dep);
                logger.debug("Found departure airport on line {}: {}", lineNumber, dep);
            }

            if (message.getArrivalAirport() == null) {
                message.setArrivalAirport(arr);
                logger.debug("Found arrival airport on line {}: {}", lineNumber, arr);
            }

            // Extract times if needed
            String depTime = routeMatcher.group(1).substring(3);
            String arrTime = routeMatcher.group(2).substring(3);
            logger.debug("Found times on line {}: Dep {}, Arr {}", lineNumber, depTime, arrTime);
        }

        // Try second route pattern for additional segments
        Matcher secondRouteMatcher = SECOND_ROUTE_PATTERN.matcher(line);
        if (secondRouteMatcher.find()) {
            String dep = secondRouteMatcher.group(1).substring(0, 3);
            String arr = secondRouteMatcher.group(2).substring(0, 3);

            // For multi-segment flights, you might want to handle this differently
            if (message.getArrivalAirport() != null && !message.getArrivalAirport().equals(arr)) {
                logger.debug("Found additional route segment on line {}: {} to {}", lineNumber, dep, arr);
                // You might want to store this in a separate field for multi-segment flights
            }
        }
    }

    private void extractConfigInfo(SsmMessage message, String line, int lineNumber) {
        Matcher configMatcher = CONFIG_PATTERN.matcher(line);
        if (configMatcher.find()) {
            logger.debug("Found aircraft configuration on line {}: {}", lineNumber, configMatcher.group(1));
            // You might want to store this in a field if your SsmMessage supports it
        }
    }
}