package com.ram.nuitparser.service;

import com.ram.nuitparser.model.telex.TelexMessage;
import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.enums.TelexType;
import com.ram.nuitparser.parser.TelexRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void parse(String rawTelex) {
        logger.info("Starting telex parsing process");
        if (rawTelex == null || rawTelex.isBlank()) {
            logger.error("Empty or null telex received - aborting parsing");
            return;
        }

        try {
            List<String> lines = rawTelex.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .toList();

            logger.debug("Processing {} non-empty lines", lines.size());

            if (lines.size() < 3) {
                logger.error("Telex message too short to parse ({} lines)", lines.size());
                return;
            }

            String sender = lines.get(0).trim();
            String receivers = lines.get(1).trim();
            String firstBodyLine = lines.get(2).trim();
            String messageBody = String.join("\n", lines.subList(2, lines.size()));

            logger.debug("Extracted sender: {}, receivers: {}", sender, receivers);
            logger.trace("First body line: {}", firstBodyLine);

            TelexType type = detectType(firstBodyLine); // FIXED: Initialize properly
            logger.info("Detected telex type: {}", type);

            // Get parsed message from router
            TelexMessage message = telexRouter.route(messageBody, type, sender, receivers);

            if (message == null) {
                logger.error("Parser returned null for telex");
                return;
            }

            logger.info("Starting enrichment for parsed message");
            enrichmentService.enrich(message);

            logger.info("Storing parsed and enriched telex");
            parsedTelexHolder.store(message, rawTelex);
            logger.info("Telex processing completed successfully");

        } catch (Exception e) {
            logger.error("Critical error parsing telex: {}", e.getMessage(), e);
        }
    }

    private TelexType detectType(String line) {
        logger.debug("Detecting telex type from: {}", line);
        if (line.toUpperCase().contains("ASM")) {
            logger.debug("Identified ASM telex type");
            return TelexType.ASM;
        }
        if (line.toUpperCase().contains("SSM")) {
            logger.debug("Identified SSM telex type");
            return TelexType.SSM;
        }
        if (line.toUpperCase().contains("MVT")) {
            logger.debug("Identified MVT telex type");
            return TelexType.MVT;
        }
        logger.warn("Unknown telex type - defaulting to UNKNOWN");
        return TelexType.UNKNOWN;
    }
}