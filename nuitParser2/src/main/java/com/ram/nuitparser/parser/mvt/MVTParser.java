package com.ram.nuitparser.parser.mvt;

import com.ram.nuitparser.model.telex.mvt.MvtMessage;
import com.ram.nuitparser.parser.TelexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MVTParser implements TelexParser<MvtMessage> {
    private static final Logger logger = LoggerFactory.getLogger(MVTParser.class);

    // Patterns for parsing MVT message components
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("([A-Z0-9]{2,3}[0-9]{1,4})/(\\d{1,2}[A-Z]{3})");
    private static final Pattern ROUTE_PATTERN = Pattern.compile("([A-Z]{3})-([A-Z]{3})");
    private static final Pattern AIRCRAFT_PATTERN = Pattern.compile("A/C:\\s*([A-Z0-9\\-]+)\\s*([A-Z0-9\\-]*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{4})Z");
    private static final Pattern STATUS_PATTERN = Pattern.compile("(DEPARTED|ARRIVED|EST|SCHEDULED|CANCELLED|DELAYED)", Pattern.CASE_INSENSITIVE);
    private static final Pattern RUNWAY_PATTERN = Pattern.compile("RUNWAY\\s*(\\d{1,2}[A-Z]?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern GATE_PATTERN = Pattern.compile("GATE\\s*([A-Z0-9]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern BAGGAGE_PATTERN = Pattern.compile("BAGGAGE:\\s*([A-Z0-9\\s]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELAY_PATTERN = Pattern.compile("DELAY\\s*(.+)", Pattern.CASE_INSENSITIVE);

    @Override
    public MvtMessage parse(String body, String sender, String receivers) {
        logger.info("Starting MVT message parsing");
        MvtMessage message = new MvtMessage();
        message.setSender(sender);
        message.setReceivers(receivers);

        String[] lines = body.split("\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            logger.debug("Processing line {}: {}", i, line);

            // Try to match different components of the MVT message

            // Check for flight designator and date (typically in first line)
            if (i == 0) {
                Matcher flightMatcher = FLIGHT_PATTERN.matcher(line);
                if (flightMatcher.find()) {
                    message.setFlightDesignator(flightMatcher.group(1));
                    logger.debug("Found flight designator: {}", flightMatcher.group(1));
                }
            }

            // Check for route (e.g., JFK-LAX)
            Matcher routeMatcher = ROUTE_PATTERN.matcher(line);
            if (routeMatcher.find()) {
                message.setDepartureAirport(routeMatcher.group(1));
                message.setArrivalAirport(routeMatcher.group(2));
                logger.debug("Found route: {} to {}", routeMatcher.group(1), routeMatcher.group(2));
            }

            // Check for aircraft information
            Matcher acMatcher = AIRCRAFT_PATTERN.matcher(line);
            if (acMatcher.find()) {
                if (acMatcher.groupCount() >= 1) {
                    message.setAircraftRegistration(acMatcher.group(1));
                    logger.debug("Found aircraft registration: {}", acMatcher.group(1));
                }
                if (acMatcher.groupCount() >= 2 && acMatcher.group(2) != null && !acMatcher.group(2).isEmpty()) {
                    message.setAircraftType(acMatcher.group(2));
                    logger.debug("Found aircraft type: {}", acMatcher.group(2));
                }
            }

            // Check for departure time
            if (line.toUpperCase().contains("DEP") || line.toUpperCase().contains("OUT")) {
                Matcher timeMatcher = TIME_PATTERN.matcher(line);
                if (timeMatcher.find()) {
                    message.setDepartureTime(timeMatcher.group(1));
                    logger.debug("Found departure time: {}", timeMatcher.group(1));
                }
            }

            // Check for arrival time
            if (line.toUpperCase().contains("ARR") || line.toUpperCase().contains("IN")) {
                Matcher timeMatcher = TIME_PATTERN.matcher(line);
                if (timeMatcher.find()) {
                    message.setArrivalTime(timeMatcher.group(1));
                    logger.debug("Found arrival time: {}", timeMatcher.group(1));
                }
            }

            // Check for status
            Matcher statusMatcher = STATUS_PATTERN.matcher(line);
            if (statusMatcher.find()) {
                message.setStatus(statusMatcher.group(1));
                logger.debug("Found status: {}", statusMatcher.group(1));
            }

            // Check for runway information
            Matcher runwayMatcher = RUNWAY_PATTERN.matcher(line);
            if (runwayMatcher.find()) {
                message.setRunway(runwayMatcher.group(1));
                logger.debug("Found runway: {}", runwayMatcher.group(1));
            }

            // Check for gate information
            Matcher gateMatcher = GATE_PATTERN.matcher(line);
            if (gateMatcher.find()) {
                message.setGate(gateMatcher.group(1));
                logger.debug("Found gate: {}", gateMatcher.group(1));
            }

            // Check for baggage information
            Matcher baggageMatcher = BAGGAGE_PATTERN.matcher(line);
            if (baggageMatcher.find()) {
                message.setBaggageCarousel(baggageMatcher.group(1));
                logger.debug("Found baggage carousel: {}", baggageMatcher.group(1));
            }

            // Check for delay reason
            Matcher delayMatcher = DELAY_PATTERN.matcher(line);
            if (delayMatcher.find()) {
                message.setDelayReason(delayMatcher.group(1));
                logger.debug("Found delay reason: {}", delayMatcher.group(1));
            }

            // Check for remarks (anything that doesn't match other patterns)
            if (i > 0 && message.getRemarks() == null &&
                    !line.isEmpty() &&
                    !routeMatcher.find() &&
                    !acMatcher.find() &&
                    !statusMatcher.find() &&
                    !runwayMatcher.find() &&
                    !gateMatcher.find() &&
                    !baggageMatcher.find() &&
                    !delayMatcher.find()) {
                message.setRemarks(line);
                logger.debug("Found remarks: {}", line);
            }
        }

        logger.info("Completed MVT message parsing for flight: {}", message.getFlightDesignator());
        return message;
    }
}