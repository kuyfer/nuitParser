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

    // Patterns for parsing LDM message components based on Avinor specification
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("([A-Z0-9]{2,3}[0-9]{1,4})/(\\d{1,2})");
    private static final Pattern AIRCRAFT_REG_PATTERN = Pattern.compile("\\.([A-Z]{5})\\.");
    private static final Pattern AIRCRAFT_TYPE_PATTERN = Pattern.compile("\\.([A-Z]\\d+Y\\d+)\\.");
    private static final Pattern SEGMENT_PATTERN = Pattern.compile("\\.(\\d+/\\d+)");
    private static final Pattern AIRPORT_PATTERN = Pattern.compile("-([A-Z]{3})\\.");
    private static final Pattern LOAD_DETAILS_PATTERN = Pattern.compile("(\\d+)/(\\d+)/(\\d+)/(\\d+)\\.(\\d+)\\.T(\\d+)\\.(\\d+)/(\\d+\\.\\d+)/(\\d+\\.\\d+)/(\\d+\\.\\d+)");
    private static final Pattern PASSENGER_PATTERN = Pattern.compile("PAX/(\\d+)/(\\d+)");
    private static final Pattern PASSENGER_DETAIL_PATTERN = Pattern.compile("PAD/(\\d+)/(\\d+)/(\\d+)");
    private static final Pattern NOTOC_PATTERN = Pattern.compile("NOTOC:\\s*([A-Z]+)");
    private static final Pattern DAA_PATTERN = Pattern.compile("DAA/([A-Z]+)");

    @Override
    public LdmMessage parse(String body, String sender, String receivers, String priority, String destination, String origin, String msgId, String header, String dblSig, String smi) {
        logger.info("Starting LDM message parsing");
        LdmMessage message = new LdmMessage();
        message.setSender(sender);
        message.setReceivers(receivers);
        message.setPriority(priority);
        message.setDestination(destination);
        message.setOrigin(origin);
        message.setMsgId(msgId);
        message.setHeader(header);

        String[] lines = body.split("\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            logger.debug("Processing line {}: {}", i, line);

            // Process first line (main flight information)
            if (i == 0) {
                parseFirstLine(line, message);
                continue;
            }

            // Process second line (load details)
            if (i == 1 && line.startsWith("-")) {
                parseLoadDetails(line, message);
                continue;
            }

            // Process special instructions
            if (line.startsWith("SI ")) {
                message.setSpecialInstructions(line.substring(3).trim());
                continue;
            }

            // Process additional load information
            if (line.contains("FRE") || line.contains("POS") || line.contains("BAG") || line.contains("TRA")) {
                parseAdditionalLoadInfo(line, message);
                continue;
            }

            // Process baggage pieces information
            if (line.contains("CHECKED BAGGAGE PIECES")) {
                parseBaggagePieces(line, message);
                continue;
            }

            // Process NOTOC status
            Matcher notocMatcher = NOTOC_PATTERN.matcher(line);
            if (notocMatcher.find()) {
                message.setNotocStatus(notocMatcher.group(1));
                continue;
            }

            // Process DAA status
            Matcher daaMatcher = DAA_PATTERN.matcher(line);
            if (daaMatcher.find()) {
                message.setDaaStatus(daaMatcher.group(1));
                continue;
            }
        }

        logger.info("Completed LDM message parsing for flight: {}", message.getFlightDesignator());
        return message;
    }

    private void parseFirstLine(String line, LdmMessage message) {
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

        // Extract aircraft type
        Matcher acTypeMatcher = AIRCRAFT_TYPE_PATTERN.matcher(line);
        if (acTypeMatcher.find()) {
            message.setAircraftType(acTypeMatcher.group(1));
            logger.debug("Found aircraft type: {}", acTypeMatcher.group(1));
        }

        // Extract segment information
        Matcher segmentMatcher = SEGMENT_PATTERN.matcher(line);
        if (segmentMatcher.find()) {
            logger.debug("Found segment info: {}", segmentMatcher.group(1));
            // You might want to store this in a separate field if needed
        }
    }

    private void parseLoadDetails(String line, LdmMessage message) {
        // Extract airport code
        Matcher airportMatcher = AIRPORT_PATTERN.matcher(line);
        if (airportMatcher.find()) {
            message.setArrivalAirport(airportMatcher.group(1));
            logger.debug("Found arrival airport: {}", airportMatcher.group(1));
        }

        // Extract load details
        Matcher loadMatcher = LOAD_DETAILS_PATTERN.matcher(line);
        if (loadMatcher.find()) {
            try {
                message.setCompartment1(loadMatcher.group(1) + "/" + loadMatcher.group(2) + "/" +
                        loadMatcher.group(3) + "/" + loadMatcher.group(4));
                message.setWeightIndex(loadMatcher.group(5));
                message.setTotalWeight(Double.parseDouble(loadMatcher.group(6)));
                message.setCompartment2(loadMatcher.group(7));
                message.setCompartment3(loadMatcher.group(8));
                message.setCompartment4(loadMatcher.group(9));

                logger.debug("Found load details: total weight={}, compartments={}/{}/{}/{}",
                        loadMatcher.group(6), loadMatcher.group(7), loadMatcher.group(8),
                        loadMatcher.group(9), loadMatcher.group(10));
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse load details: {}", line);
            }
        }

        // Extract passenger information
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

        // Extract detailed passenger information
        Matcher padMatcher = PASSENGER_DETAIL_PATTERN.matcher(line);
        if (padMatcher.find()) {
            try {
                message.setAdultPassengers(Integer.parseInt(padMatcher.group(1)));
                message.setChildPassengers(Integer.parseInt(padMatcher.group(2)));
                message.setInfantPassengers(Integer.parseInt(padMatcher.group(3)));
                logger.debug("Found passenger detail: adults={}, children={}, infants={}",
                        padMatcher.group(1), padMatcher.group(2), padMatcher.group(3));
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse passenger details: {}", line);
            }
        }
    }

    private void parseAdditionalLoadInfo(String line, LdmMessage message) {
        // Parse additional load information like FRE, POS, BAG, TRA
        String[] parts = line.split("\\s+");
        try {
            for (int i = 0; i < parts.length; i++) {
                if ("FRE".equals(parts[i]) && i + 1 < parts.length) {
                    message.setFreightWeight(Double.parseDouble(parts[i + 1]));
                } else if ("POS".equals(parts[i]) && i + 1 < parts.length) {
                    // Position data, might not need to store
                } else if ("BAG".equals(parts[i]) && i + 1 < parts.length) {
                    message.setBaggageWeight(Double.parseDouble(parts[i + 1]));
                } else if ("TRA".equals(parts[i]) && i + 1 < parts.length) {
                    // Transit data, might not need to store
                }
            }
            logger.debug("Found additional load info: freight={}, baggage={}",
                    message.getFreightWeight(), message.getBaggageWeight());
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse additional load info: {}", line);
        }
    }

    private void parseBaggagePieces(String line, LdmMessage message) {
        // Parse checked baggage pieces information
        logger.debug("Found baggage pieces info: {}", line);
        // You might want to store this in a separate field if needed
    }
}