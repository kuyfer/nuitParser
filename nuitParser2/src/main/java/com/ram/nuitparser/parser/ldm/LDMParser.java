package com.ram.nuitparser.parser.ldm;

import com.ram.nuitparser.model.telex.ldm.LdmMessage;
import com.ram.nuitparser.parser.TelexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LDMParser implements TelexParser<LdmMessage> {
    private static final Logger logger = LoggerFactory.getLogger(LDMParser.class);

    // Patterns for parsing LDM message components
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("([A-Z0-9]{2,3}[0-9]{1,4})/([0-9]{1,2}[A-Z]{3})");
    private static final Pattern ROUTE_PATTERN = Pattern.compile("([A-Z]{3})-([A-Z]{3})");
    private static final Pattern PASSENGER_PATTERN = Pattern.compile("PAX:\\s*(\\d+)/(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CARGO_PATTERN = Pattern.compile("CARGO:\\s*([0-9.]+)\\s*(KG|kg|Kg)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MAIL_PATTERN = Pattern.compile("MAIL:\\s*([0-9.]+)\\s*(KG|kg|Kg)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOTAL_WEIGHT_PATTERN = Pattern.compile("TOTAL\\s+WEIGHT:\\s*([0-9.]+)\\s*(KG|kg|Kg)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DISTRIBUTION_PATTERN = Pattern.compile("DISTRIBUTION:\\s*([A-Z0-9\\-]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SPECIAL_LOADS_PATTERN = Pattern.compile("SPECIAL\\s+LOADS?:\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern AIRCRAFT_PATTERN = Pattern.compile("A/C:\\s*([A-Z0-9\\-]+)\\s*([A-Z0-9\\-]*)", Pattern.CASE_INSENSITIVE);

    @Override
    public LdmMessage parse(String body, String sender, String receivers) {
        logger.info("Starting LDM message parsing");
        LdmMessage message = new LdmMessage();
        message.setSender(sender);
        message.setReceivers(receivers);

        String[] lines = body.split("\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            logger.debug("Processing line {}: {}", i, line);

            // Try to match different components of the LDM message
            if (i == 0) {
                // First line typically contains flight designator and date
                Matcher flightMatcher = FLIGHT_PATTERN.matcher(line);
                if (flightMatcher.find()) {
                    message.setFlightDesignator(flightMatcher.group(1));
                    message.setDateOfFlight(flightMatcher.group(2));
                    logger.debug("Found flight: {} on date: {}", flightMatcher.group(1), flightMatcher.group(2));
                }
            }

            // Check for route (e.g., AMS-NRT)
            Matcher routeMatcher = ROUTE_PATTERN.matcher(line);
            if (routeMatcher.find()) {
                message.setDepartureAirport(routeMatcher.group(1));
                message.setArrivalAirport(routeMatcher.group(2));
                logger.debug("Found route: {} to {}", routeMatcher.group(1), routeMatcher.group(2));
            }

            // Check for passenger information
            Matcher paxMatcher = PASSENGER_PATTERN.matcher(line);
            if (paxMatcher.find()) {
                try {
                    message.setPassengerCount(Integer.parseInt(paxMatcher.group(1)));
                    message.setPassengerCapacity(Integer.parseInt(paxMatcher.group(2)));
                    logger.debug("Found passenger info: {}/{}", paxMatcher.group(1), paxMatcher.group(2));
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse passenger numbers: {}", line);
                }
            }

            // Check for cargo information
            Matcher cargoMatcher = CARGO_PATTERN.matcher(line);
            if (cargoMatcher.find()) {
                try {
                    message.setCargoWeight(Double.parseDouble(cargoMatcher.group(1)));
                    logger.debug("Found cargo weight: {}", cargoMatcher.group(1));
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse cargo weight: {}", line);
                }
            }

            // Check for mail information
            Matcher mailMatcher = MAIL_PATTERN.matcher(line);
            if (mailMatcher.find()) {
                try {
                    message.setMailWeight(Double.parseDouble(mailMatcher.group(1)));
                    logger.debug("Found mail weight: {}", mailMatcher.group(1));
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse mail weight: {}", line);
                }
            }

            // Check for total weight
            Matcher totalWeightMatcher = TOTAL_WEIGHT_PATTERN.matcher(line);
            if (totalWeightMatcher.find()) {
                try {
                    message.setTotalWeight(Double.parseDouble(totalWeightMatcher.group(1)));
                    logger.debug("Found total weight: {}", totalWeightMatcher.group(1));
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse total weight: {}", line);
                }
            }

            // Check for weight distribution
            Matcher distMatcher = DISTRIBUTION_PATTERN.matcher(line);
            if (distMatcher.find()) {
                message.setWeightDistribution(distMatcher.group(1));
                logger.debug("Found weight distribution: {}", distMatcher.group(1));
            }

            // Check for special loads
            Matcher specialMatcher = SPECIAL_LOADS_PATTERN.matcher(line);
            if (specialMatcher.find()) {
                message.setSpecialLoads(specialMatcher.group(1));
                logger.debug("Found special loads: {}", specialMatcher.group(1));
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
        }

        logger.info("Completed LDM message parsing for flight: {}", message.getFlightDesignator());
        return message;
    }
}