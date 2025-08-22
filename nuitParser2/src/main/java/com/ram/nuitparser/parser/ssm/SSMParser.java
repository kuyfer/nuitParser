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

    // Patterns for parsing SSM message components
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("([A-Z0-9]{2,3}[0-9]{1,4})");
    private static final Pattern ROUTE_PATTERN = Pattern.compile("([A-Z]{3})-([A-Z]{3})");
    private static final Pattern AIRCRAFT_PATTERN = Pattern.compile("A/C:\\s*([A-Z0-9]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{1,2}[A-Z]{3}\\d{2,4})");
    private static final Pattern DAYS_PATTERN = Pattern.compile("([1-7]+|DAILY|WEEKDAYS|WEEKENDS)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{4})Z");
    private static final Pattern EFFECTIVE_PATTERN = Pattern.compile("EFF:\\s*(\\d{1,2}[A-Z]{3}\\d{2,4})", Pattern.CASE_INSENSITIVE);
    private static final Pattern DISCONTINUATION_PATTERN = Pattern.compile("UNTIL:\\s*(\\d{1,2}[A-Z]{3}\\d{2,4})", Pattern.CASE_INSENSITIVE);

    @Override
    public SsmMessage parse(String body, String priority, String destination, String origin, String msgId, String header, String dblSig, String smi) {
        logger.info("Starting SSM message parsing");
        SsmMessage message = new SsmMessage();
        message.setPriority(priority);
        message.setDestination(destination);
        message.setOrigin(origin);
        message.setMsgId(msgId);

        String[] lines = body.split("\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            logger.debug("Processing line {}: {}", i, line);

            // Try to match different components of the SSM message

            // Check for flight designator (typically in first line)
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

            // Check for aircraft type
            Matcher acMatcher = AIRCRAFT_PATTERN.matcher(line);
            if (acMatcher.find()) {
                message.setAircraftType(acMatcher.group(1));
                logger.debug("Found aircraft type: {}", acMatcher.group(1));
            }

            // Check for the effective date
            Matcher effMatcher = EFFECTIVE_PATTERN.matcher(line);
            if (effMatcher.find()) {
                message.setEffectiveDate(effMatcher.group(1));
                logger.debug("Found effective date: {}", effMatcher.group(1));
            }

            // Check for discontinuation date
            Matcher untilMatcher = DISCONTINUATION_PATTERN.matcher(line);
            if (untilMatcher.find()) {
                message.setDiscontinuationDate(untilMatcher.group(1));
                logger.debug("Found discontinuation date: {}", untilMatcher.group(1));
            }

            // Check for days of operation
            Matcher daysMatcher = DAYS_PATTERN.matcher(line);
            if (daysMatcher.find()) {
                message.setDaysOfOperation(daysMatcher.group(1));
            }
        }

        logger.info("Completed SSM message parsing for flight: {}", message.getFlightDesignator());
        return message;
    }
}