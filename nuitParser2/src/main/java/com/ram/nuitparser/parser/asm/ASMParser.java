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

    private static final Pattern ACTION_PATTERN = Pattern.compile("\\b(NEW|CNL|RIN|RPL|ADM|CON|EQT|FLT|RRT|TIM)\\b");
    private static final Pattern FLIGHT_PATTERN = Pattern.compile("\\b([A-Z]{2}\\d{1,4})/(\\d{1,2}[A-Z]{3}\\d{2})\\b");
    private static final Pattern DEI_PATTERN = Pattern.compile("\\b(\\d+/[A-Z0-9]+)\\b");
    private static final Pattern AIRCRAFT_PATTERN = Pattern.compile("\\b([A-Z]\\w{2,3})\\s*\\.([A-Z]\\d+)\\b");
    private static final Pattern AIRPORT_PATTERN = Pattern.compile("\\b([A-Z]{3})(\\d{4,6})\\b");

    public AsmMessage parse(String body, String sender, String receivers) {
        logger.info("Starting ASM message parsing");
        logger.debug("Sender: {}, Receivers: {}", sender, receivers);

        if (body == null || body.isBlank()) {
            logger.warn("Empty telex body received - skipping parsing");
            return null;
        }

        AsmMessage message = new AsmMessage();
        message.setSender(sender);
        message.setReceivers(receivers);
        message.setRawBody(body);

        List<String> deiList = new ArrayList<>();
        List<String> airports = new ArrayList<>();
        List<String> times = new ArrayList<>();

        logger.debug("Processing {} lines in telex body", body.split("\\n").length);
        int lineNum = 0;
        for (String line : body.split("\\n")) {
            lineNum++;
            line = line.trim();
            if (line.isEmpty()) {
                logger.trace("Skipping empty line {}", lineNum);
                continue;
            }

            logger.debug("Processing line {}: {}", lineNum, line);
            extractAction(message, line);
            extractFlightInfo(message, line);
            extractDEIs(deiList, line);
            extractAircraftInfo(message, line);
            extractAirportsAndTimes(airports, times, line);
        }

        // Assign departure/arrival airports and times
        if (!airports.isEmpty()) {
            message.setDepartureAirport(airports.get(0));
            logger.debug("Set departure airport: {}", airports.get(0));
            if (airports.size() > 1) {
                message.setArrivalAirport(airports.get(1));
                logger.debug("Set arrival airport: {}", airports.get(1));
            }
        }
        if (!times.isEmpty()) {
            message.setDepartureTime(times.get(0));
            logger.debug("Set departure time: {}", times.get(0));
            if (times.size() > 1) {
                message.setArrivalTime(times.get(1));
                logger.debug("Set arrival time: {}", times.get(1));
            }
        }

        message.setDeIdentifiers(deiList);
        logger.info("Successfully parsed ASM message. Found {} DEIs", deiList.size());
        return message;
    }

    private void extractAction(AsmMessage message, String line) {
        Matcher actionMatcher = ACTION_PATTERN.matcher(line);
        if (actionMatcher.find() && message.getAction() == null) {
            String action = actionMatcher.group(1);
            message.setAction(action);
            logger.debug("Extracted action code: {}", action);
        }
    }

    private void extractFlightInfo(AsmMessage message, String line) {
        if (message.getFlightDesignator() != null) {
            logger.trace("Flight designator already set - skipping extraction");
            return;
        }

        Matcher flightMatcher = FLIGHT_PATTERN.matcher(line);
        if (flightMatcher.find()) {
            String designator = flightMatcher.group(1);
            String date = flightMatcher.group(2);
            message.setFlightDesignator(designator);
            message.setFlightDate(date);
            logger.info("Extracted flight designator: {} and date: {}", designator, date);
        }
    }

    private void extractDEIs(List<String> deiList, String line) {
        Matcher deiMatcher = DEI_PATTERN.matcher(line);
        int count = 0;
        while (deiMatcher.find()) {
            String dei = deiMatcher.group(1);
            deiList.add(dei);
            count++;
            logger.trace("Extracted DEI: {}", dei);
        }
        if (count > 0) {
            logger.debug("Extracted {} DEIs from line", count);
        }
    }

    private void extractAircraftInfo(AsmMessage message, String line) {
        if (message.getAircraftType() != null) {
            logger.trace("Aircraft type already set - skipping extraction");
            return;
        }

        Matcher acMatcher = AIRCRAFT_PATTERN.matcher(line);
        if (acMatcher.find()) {
            String type = acMatcher.group(1);
            String version = acMatcher.group(2);
            message.setAircraftType(type);
            message.setEquipmentVersion(version);
            logger.info("Extracted aircraft type: {} and version: {}", type, version);
        }
    }

    private void extractAirportsAndTimes(List<String> airports, List<String> times, String line) {
        Matcher aptMatcher = AIRPORT_PATTERN.matcher(line);
        int count = 0;
        while (aptMatcher.find()) {
            String airport = aptMatcher.group(1);
            String time = aptMatcher.group(2);

            if (airport != null && !airport.isEmpty()) {
                airports.add(airport);
                logger.trace("Found airport: {}", airport);
            }
            if (time != null && !time.isEmpty()) {
                times.add(time);
                logger.trace("Found time: {}", time);
            }
            count++;
        }
        if (count > 0) {
            logger.debug("Extracted {} airport/time pairs from line", count);
        }
    }
}