package com.ram.nuitparser.parser.asm;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.parser.TelexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ASMParser implements TelexParser<AsmMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ASMParser.class);

    // Patterns for parsing ASM message components based on Avinor specification
    private static final Pattern ACTION_PATTERN = Pattern.compile("\\b(RPL|NEW|CNL|CHG|COR|RIN|ADM|CON|EQT|FLT|RRT|TIM)\\b");
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("([A-Z]{2})(\\d{2,4})([A-Z]?)\\s*/\\s*(\\d{1,2}[A-Z]{3})");
    private static final Pattern AIRCRAFT_PATTERN = Pattern.compile("\\b([A-Z]\\d{3}|[A-Z]{2}\\d{2}|[A-Z]\\d{2}[A-Z])\\b");
    private static final Pattern EQUIPMENT_PATTERN = Pattern.compile("\\.([A-Z]\\d+Y\\d+)\\b");
    private static final Pattern DEI_PATTERN = Pattern.compile("\\b(\\d+/[A-Z]{2,3})\\b");
    private static final Pattern AIRPORT_TIME_PATTERN = Pattern.compile("\\b([A-Z]{3})(\\d{4})\\b");
    private static final Pattern DAYS_OPERATION_PATTERN = Pattern.compile("\\b([1-7]+)\\b");
    private static final Pattern PERIOD_OPERATION_PATTERN = Pattern.compile("\\b(\\d{1,2}[A-Z]{3})\\s*(\\d{1,2}[A-Z]{3})\\b");
    private static final Pattern CREW_PATTERN = Pattern.compile("\\b([A-Z]{3,6})\\b");
    private static final Pattern MEAL_PATTERN = Pattern.compile("\\b([A-Z])\\b");

    @Override
    public AsmMessage parse(String body, String priority, String destination, String origin, String msgId, String header, String dblSig, String smi) {
        logger.info("Starting ASM message parsing");
        AsmMessage message = new AsmMessage();

        // Set only the common header fields (sender and receivers are no longer in the model)
        message.setPriority(priority);
        message.setDestination(destination);
        message.setOrigin(origin);
        message.setMsgId(msgId);

        List<String> deiList = new ArrayList<>();

        String[] lines = body.split("\\n");
        logger.debug("Processing {} lines in ASM message", lines.length);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            logger.debug("Processing line {}: {}", i, line);

            // Extract action code (RPL, NEW, CNL, etc.)
            extractAction(message, line);

            // Extract flight information
            extractFlightInfo(message, line);

            // Extract aircraft information
            extractAircraftInfo(message, line);

            // Extract DEIs (Data Element Identifiers)
            extractDEIs(deiList, line);

            // Extract airport and time information
            extractAirportAndTimeInfo(message, line);

            // Extract operational information
            extractOperationalInfo(message, line);
        }

        message.setDeIdentifiers(deiList);
        logger.info("Completed ASM message parsing for flight: {}", message.getFlightDesignator());
        return message;
    }

    private void extractAction(AsmMessage message, String line) {
        if (message.getAction() != null) return;

        Matcher actionMatcher = ACTION_PATTERN.matcher(line);
        if (actionMatcher.find()) {
            String actionCode = actionMatcher.group(1);

            // Set the action code - the description will be available via getActionDescription()
            message.setAction(actionCode);
            logger.debug("Found action: {} - {}", actionCode, message.getActionDescription());
        }
    }

    private void extractFlightInfo(AsmMessage message, String line) {
        if (message.getFlightDesignator() != null) return;

        Matcher flightMatcher = FLIGHT_PATTERN.matcher(line);
        if (flightMatcher.find()) {
            String airlineCode = flightMatcher.group(1);
            String flightNumber = flightMatcher.group(2);
            String flightSuffix = flightMatcher.group(3);
            String date = flightMatcher.group(4);

            message.setFlightDesignator(airlineCode + flightNumber + flightSuffix);
            message.setFlightNumber(flightNumber);
            message.setFlightSuffix(flightSuffix);
            message.setFlightDate(date);

            logger.debug("Found flight: {} on date: {}", message.getFlightDesignator(), date);
        }
    }

    private void extractAircraftInfo(AsmMessage message, String line) {
        if (message.getAircraftType() != null) return;

        // Extract aircraft type
        Matcher aircraftMatcher = AIRCRAFT_PATTERN.matcher(line);
        if (aircraftMatcher.find()) {
            message.setAircraftType(aircraftMatcher.group(1));
            logger.debug("Found aircraft type: {}", aircraftMatcher.group(1));
        }

        // Extract equipment version
        Matcher equipmentMatcher = EQUIPMENT_PATTERN.matcher(line);
        if (equipmentMatcher.find()) {
            message.setEquipmentVersion(equipmentMatcher.group(1));
            logger.debug("Found equipment version: {}", equipmentMatcher.group(1));
        }
    }

    private void extractDEIs(List<String> deiList, String line) {
        Matcher deiMatcher = DEI_PATTERN.matcher(line);
        while (deiMatcher.find()) {
            deiList.add(deiMatcher.group(1));
            logger.debug("Found DEI: {}", deiMatcher.group(1));
        }
    }

    private void extractAirportAndTimeInfo(AsmMessage message, String line) {
        Matcher airportTimeMatcher = AIRPORT_TIME_PATTERN.matcher(line);
        List<String> airports = new ArrayList<>();
        List<String> times = new ArrayList<>();

        while (airportTimeMatcher.find()) {
            String airport = airportTimeMatcher.group(1);
            String time = airportTimeMatcher.group(2);

            airports.add(airport);
            times.add(time);

            logger.debug("Found airport: {} with time: {}", airport, time);
        }

        // Assign departure and arrival airports/times
        if (airports.size() >= 2 && times.size() >= 2) {
            message.setDepartureAirport(airports.get(0));
            message.setDepartureTime(times.get(0));
            message.setArrivalAirport(airports.get(1));
            message.setArrivalTime(times.get(1));
        }
    }

    private void extractOperationalInfo(AsmMessage message, String line) {
        // Extract days of operation
        Matcher daysMatcher = DAYS_OPERATION_PATTERN.matcher(line);
        if (daysMatcher.find() && message.getDaysOfOperation() == null) {
            message.setDaysOfOperation(daysMatcher.group(1));
            logger.debug("Found days of operation: {}", daysMatcher.group(1));
        }

        // Extract period of operation
        Matcher periodMatcher = PERIOD_OPERATION_PATTERN.matcher(line);
        if (periodMatcher.find() && message.getPeriodOfOperation() == null) {
            String period = periodMatcher.group(1) + " " + periodMatcher.group(2);
            message.setPeriodOfOperation(period);
            logger.debug("Found period of operation: {}", period);
        }

        // Extract crew information
        Matcher crewMatcher = CREW_PATTERN.matcher(line);
        if (crewMatcher.find() && message.getCrewInformation() == null) {
            message.setCrewInformation(crewMatcher.group(1));
            logger.debug("Found crew information: {}", crewMatcher.group(1));
        }

        // Extract meal service information
        Matcher mealMatcher = MEAL_PATTERN.matcher(line);
        if (mealMatcher.find() && message.getMealService() == null) {
            message.setMealService(mealMatcher.group(1));
            logger.debug("Found meal service: {}", mealMatcher.group(1));
        }
    }
}