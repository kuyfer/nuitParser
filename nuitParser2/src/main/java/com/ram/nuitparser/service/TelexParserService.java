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

            String firstBodyLine = lines.get(0).trim();

            // Determine type from SMI if available, otherwise from first body line
            TelexType type;
            if (headers.containsKey("SMI")) {
                type = detectType(headers.get("SMI"));
                logger.debug("Type detected from SMI: {}", type);
            } else {
                type = detectType(firstBodyLine);
                logger.debug("Type detected from body: {}", type);
            }

            // Extract additional headers
            String priority = headers.getOrDefault("PRIORITY", "");
            String destination = headers.getOrDefault("DESTINATION", "");
            String origin = headers.getOrDefault("ORIGIN", "");
            String msgId = headers.getOrDefault("MSGID", "");
            String headerValue = headers.getOrDefault("HEADER", "");
            String dblSig = headers.getOrDefault("DBLSIG", "");
            String smi = headers.getOrDefault("SMI", "");

            // Pass all extracted information to the router
            TelexMessage message = telexRouter.route(telexBody, type,
                    priority, destination, origin,
                    msgId, headerValue, dblSig, smi);

            if (message == null) {
                logger.error("Router returned null for telex");
                return;
            }

            enrichmentService.enrich(message);
            parsedTelexHolder.store(message, telexBody);

            logger.info("Telex processed successfully");

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
                if (currentHeader != null) {
                    headers.put(currentHeader, currentValue.toString().trim());
                }

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
                if (!currentValue.isEmpty()) currentValue.append(" ");
                currentValue.append(line.trim());
            }
        }

        if (currentHeader != null) {
            headers.put(currentHeader, currentValue.toString().trim());
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
        return telexBody.toString().trim();
    }

    private String extractSender(Map<String, String> headers) {
        String sender = headers.get("ORIGIN");
        if (sender != null && !sender.isBlank()) return sender.trim();

        if (headers.containsKey("MSGID")) {
            return headers.get("MSGID").split(" ")[0];
        }

        return "UNKNOWN";
    }

    private String extractReceivers(Map<String, String> headers) {
        String destination = headers.get("DESTINATION");
        if (destination != null && !destination.isBlank()) return destination.trim();

        StringBuilder receivers = new StringBuilder();
        for (String key : headers.keySet()) {
            if (key.startsWith("STX,")) {
                if (!receivers.isEmpty()) receivers.append(" ");
                receivers.append(key.substring(4));
            }
        }

        return !receivers.isEmpty() ? receivers.toString() : "UNKNOWN";
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