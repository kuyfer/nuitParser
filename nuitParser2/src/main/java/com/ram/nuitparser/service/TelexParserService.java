package com.ram.nuitparser.service;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.enums.TelexType;
import com.ram.nuitparser.parser.TelexRouter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelexParserService {

    private final TelexRouter telexRouter;
    private final EnrichmentService enrichmentService;
    private final ParsedTelexHolder parsedTelexHolder;

    // Add required dependencies via constructor injection
    public TelexParserService(
            TelexRouter telexRouter,
            EnrichmentService enrichmentService,
            ParsedTelexHolder parsedTelexHolder
    ) {
        this.telexRouter = telexRouter;
        this.enrichmentService = enrichmentService;
        this.parsedTelexHolder = parsedTelexHolder;
    }

    public void parse(String rawTelex) {
        try {
            List<String> lines = rawTelex.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .toList();

            if (lines.size() < 3) {
                System.err.println("Telex message too short to parse.");
                return;
            }

            String sender = lines.get(0).trim();
            String receivers = lines.get(1).trim();
            String firstBodyLine = lines.get(2).trim();
            String messageBody = String.join("\n", lines.subList(2, lines.size()));

            TelexType telexType = detectType(firstBodyLine);

            // Get parsed message from router
            AsmMessage message = telexRouter.route(messageBody, telexType, sender, receivers);

            if (message == null) {
                System.err.println("Parser returned null for telex");
                return;
            }

            // Perform enrichment
            enrichmentService.enrich(message);

            // Store final result
            parsedTelexHolder.store(message, rawTelex);

        } catch (Exception e) {
            System.err.println("Error parsing telex: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private TelexType detectType(String line) {
        if (line.toUpperCase().contains("ASM")) return TelexType.ASM;
        if (line.toUpperCase().contains("SSM")) return TelexType.SSM;
        if (line.toUpperCase().contains("MVT")) return TelexType.MVT;
        return TelexType.UNKNOWN;
    }
}