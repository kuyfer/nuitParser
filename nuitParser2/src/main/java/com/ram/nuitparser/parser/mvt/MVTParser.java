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

    // Patterns for parsing MVT message components based on Avinor specification
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("([A-Z0-9]{2,3}[0-9]{1,4})/(\\d{1,2})\\.");
    private static final Pattern AIRCRAFT_REG_PATTERN = Pattern.compile("\\.([A-Z]{5})\\.");
    private static final Pattern AIRPORT_PATTERN = Pattern.compile("\\.([A-Z]{3})");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{4})");
    private static final Pattern DELAY_PATTERN = Pattern.compile("DELAY\\s+(.+)", Pattern.CASE_INSENSITIVE);

    @Override
    public MvtMessage parse(String body, String priority, String destination, String origin, String msgId, String header, String dblSig, String smi) {
        logger.info("Starting MVT message parsing");
        MvtMessage message = new MvtMessage();

        // Set only the common header fields (sender and receivers are no longer in the model)
        message.setPriority(priority);
        message.setDestination(destination);
        message.setOrigin(origin);
        message.setMsgId(msgId);


        String[] lines = body.split("\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            logger.debug("Processing line {}: {}", i, line);

            // Process first line (main flight information)
            if (i == 0) {
                parseFirstLine(line, message);
                continue;
            }

            // Process times and status
            if (line.contains("AD") || line.contains("EO") || line.contains("EA")) {
                parseTimesAndStatus(line, message);
                continue;
            }

            // Process delay information
            if (line.toUpperCase().contains("DELAY")) {
                parseDelayInfo(line, message);
                continue;
            }

            // Process remarks (anything that doesn't match other patterns)
            if (message.getRemarks() == null && !line.isEmpty()) {
                message.setRemarks(line);
                logger.debug("Found remarks: {}", line);
            }
        }

        logger.info("Completed MVT message parsing for flight: {}", message.getFlightDesignator());
        return message;
    }

    private void parseFirstLine(String line, MvtMessage message) {
        // Extract flight designator and date
        Matcher flightMatcher = FLIGHT_PATTERN.matcher(line);
        if (flightMatcher.find()) {
            message.setFlightDesignator(flightMatcher.group(1));
            message.setDateOfFlight(flightMatcher.group(2));
            logger.debug("Found flight: {} on day: {}", flightMatcher.group(1), flightMatcher.group(2));
        }

        // Extract aircraft registration
        Matcher acRegMatcher = AIRCRAFT_REG_PATTERN.matcher(line);
        if (acRegMatcher.find()) {
            message.setAircraftRegistration(acRegMatcher.group(1));
            logger.debug("Found aircraft registration: {}", acRegMatcher.group(1));
        }

        // Extract airports
        Matcher airportMatcher = AIRPORT_PATTERN.matcher(line);
        int airportCount = 0;
        while (airportMatcher.find()) {
            if (airportCount == 0) {
                message.setDepartureAirport(airportMatcher.group(1));
                logger.debug("Found departure airport: {}", airportMatcher.group(1));
            } else if (airportCount == 1) {
                message.setArrivalAirport(airportMatcher.group(1));
                logger.debug("Found arrival airport: {}", airportMatcher.group(1));
            }
            airportCount++;
        }
    }

    private void parseTimesAndStatus(String line, MvtMessage message) {
        // Extract status codes and times
        String[] parts = line.split("\\s+");
        for (String s : parts) {
            String part = s.toUpperCase();

            if (part.startsWith("AD")) {
                // Actual Departure (off-block time)
                Matcher timeMatcher = TIME_PATTERN.matcher(part.substring(2));
                if (timeMatcher.find()) {
                    message.setActualOffBlockTime(timeMatcher.group(1));
                    message.setMovementStatus("DEPARTED");
                    logger.debug("Found actual off-block time: {}", timeMatcher.group(1));
                }
            } else if (part.startsWith("EO")) {
                // Estimated Off-block time
                // Not used in this implementation, but could be stored if needed
                Matcher timeMatcher = TIME_PATTERN.matcher(part.substring(2));
                if (timeMatcher.find()) {
                    logger.debug("Found estimated off-block time: {}", timeMatcher.group(1));
                }
            } else if (part.startsWith("EA")) {
                // Estimated Arrival (in-block time)
                Matcher timeMatcher = TIME_PATTERN.matcher(part.substring(2));
                if (timeMatcher.find()) {
                    message.setActualInBlockTime(timeMatcher.group(1));
                    message.setMovementStatus("ARRIVED");
                    logger.debug("Found estimated arrival time: {}", timeMatcher.group(1));
                }
            } else if (part.length() == 4 && part.matches("\\d{4}")) {
                // Standalone time (could be takeoff or landing time)
                if (message.getActualTakeoffTime() == null) {
                    message.setActualTakeoffTime(part);
                    logger.debug("Found takeoff time: {}", part);
                } else if (message.getActualLandingTime() == null) {
                    message.setActualLandingTime(part);
                    logger.debug("Found landing time: {}", part);
                }
            }
        }
    }

    private void parseDelayInfo(String line, MvtMessage message) {
        // Extract delay reason
        Matcher delayMatcher = DELAY_PATTERN.matcher(line);
        if (delayMatcher.find()) {
            message.setDelayReason(delayMatcher.group(1));
            logger.debug("Found delay reason: {}", delayMatcher.group(1));
        }
    }
}