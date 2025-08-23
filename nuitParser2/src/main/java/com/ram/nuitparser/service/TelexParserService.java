package com.ram.nuitparser.service;

import com.ram.nuitparser.model.telex.TelexMessage;
import com.ram.nuitparser.enums.TelexType;
import com.ram.nuitparser.parser.TelexRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelexParserService {
    private static final Logger logger = LoggerFactory.getLogger(TelexParserService.class);

    private final TelexRouter telexRouter;
    private final EnrichmentService enrichmentService;
    private final ParsedTelexHolder parsedTelexHolder;

    public TelexParserService(
            TelexRouter telexRouter,
            EnrichmentService enrichmentService,
            ParsedTelexHolder parsedTelexHolder
    ) {
        this.telexRouter = telexRouter;
        this.enrichmentService = enrichmentService;
        this.parsedTelexHolder = parsedTelexHolder;
        logger.info("TelexParserService initialized");
    }

    public void parse(String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            logger.error("Empty or null telex content - aborting parsing");
            return;
        }

        try {
            Map<String, String> headers = extractHeaders(rawContent);
            String telexBody = extractTelexBody(rawContent);

            if (telexBody.isBlank()) {
                logger.warn("No telex body found");
                return;
            }

            List<String> lines = telexBody.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .toList();

            if (lines.isEmpty()) {
                logger.warn("Telex body is empty after trimming blank lines");
                return;
            }

            String firstBodyLine = lines.getFirst().trim();

            // Determine type from SMI if available, otherwise from first body line
            TelexType type = detectTypeFromHeadersOrBody(headers, firstBodyLine);

            // Extract headers with logging
            String priority = headers.getOrDefault("PRIORITY", "");
            String destination = extractReceivers(headers);
            String origin = headers.getOrDefault("ORIGIN", "");
            String msgId = headers.getOrDefault("MSGID", "");
            String headerValue = headers.getOrDefault("HEADER", "");
            String dblSig = headers.getOrDefault("DBLSIG", "");
            String smi = headers.getOrDefault("SMI", "");

            logger.debug("Parsed headers - Priority: {}, Destination: {}, Origin: {}, MsgId: {}",
                    priority, destination, origin, msgId);

            // Route the message
            TelexMessage message = telexRouter.route(telexBody, type,
                    priority, destination, origin,
                    msgId, headerValue, dblSig, smi);

            if (message == null) {
                logger.error("Router returned null for telex");
                return;
            }

            // Enrich and store the message
            enrichmentService.enrich(message);
            parsedTelexHolder.store(message, telexBody);

            logger.info("Successfully processed {} telex for flight: {}", type, message.getFlightDesignator());

        } catch (Exception e) {
            logger.error("Critical error parsing telex: {}", e.getMessage(), e);
        }
    }

    private Map<String, String> extractHeaders(String content) {
        Map<String, String> headers = new HashMap<>();
        String[] lines = content.split("\\r?\\n");
        String currentHeader = null;
        StringBuilder currentValue = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith("=")) {
                // Save previous header
                if (currentHeader != null) {
                    headers.put(currentHeader, currentValue.toString().trim());
                    logger.debug("Header extracted: {} = {}", currentHeader, currentValue.toString().trim());
                }

                // Start new header
                String headerLine = line.substring(1);
                int spaceIndex = headerLine.indexOf(' ');

                if (spaceIndex > 0) {
                    currentHeader = headerLine.substring(0, spaceIndex).trim();
                    currentValue = new StringBuilder(headerLine.substring(spaceIndex + 1).trim());
                } else {
                    currentHeader = headerLine.trim();
                    currentValue = new StringBuilder();
                }
            } else if (currentHeader != null) {
                // Continue current header
                if (!currentValue.isEmpty()) currentValue.append(" ");
                currentValue.append(line.trim());
            }
        }

        // Save the last header
        if (currentHeader != null) {
            headers.put(currentHeader, currentValue.toString().trim());
            logger.debug("Final header extracted: {} = {}", currentHeader, currentValue.toString().trim());
        }

        return headers;
    }

    private String extractTelexBody(String content) {
        String[] lines = content.split("\\r?\\n");
        StringBuilder telexBody = new StringBuilder();
        boolean inTextSection = false;

        for (String line : lines) {
            if (line.startsWith("=TEXT")) {
                inTextSection = true;
                continue;
            }
            if (inTextSection) {
                if (line.startsWith("=")) break;
                telexBody.append(line).append("\n");
            }
        }

        String body = telexBody.toString().trim();
        logger.debug("Extracted telex body: {}", body);
        return body;
    }

    private String extractReceivers(Map<String, String> headers) {
        String destinationBlock = headers.get("DESTINATION");
        if (destinationBlock == null || destinationBlock.isBlank()) {
            logger.debug("No DESTINATION header found");
            return "";
        }

        logger.debug("Original DESTINATION block: {}", destinationBlock);

        // Strip TYPE B (case insensitive)
        String cleaned = destinationBlock.replaceAll("(?i)TYPE B", "").trim();
        logger.debug("After removing TYPE B: {}", cleaned);

        // Extract IDs from STX,<ID>
        Pattern pattern = Pattern.compile("STX,([A-Z0-9]+)");
        Matcher matcher = pattern.matcher(cleaned);

        StringBuilder receivers = new StringBuilder();
        while (matcher.find()) {
            String id = matcher.group(1).trim();
            if (!id.isEmpty()) {
                if (!receivers.isEmpty()) receivers.append(",");
                receivers.append(id);
                logger.debug("Found receiver ID: {}", id);
            }
        }

        String result = receivers.toString();
        logger.info("Extracted receiver IDs: {}", result);
        return result;
    }

    private TelexType detectTypeFromHeadersOrBody(Map<String, String> headers, String firstBodyLine) {
        if (headers.containsKey("SMI")) {
            TelexType type = detectType(headers.get("SMI"));
            logger.debug("Type detected from SMI: {}", type);
            return type;
        } else {
            TelexType type = detectType(firstBodyLine);
            logger.debug("Type detected from body: {}", type);
            return type;
        }
    }

    private TelexType detectType(String line) {
        String upper = line.toUpperCase();
        if (upper.contains("ASM")) return TelexType.ASM;
        if (upper.contains("SSM")) return TelexType.SSM;
        if (upper.contains("MVT")) return TelexType.MVT;
        if (upper.contains("LDM")) return TelexType.LDM;
        return TelexType.UNKNOWN;
    }
}