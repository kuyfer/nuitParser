package com.ram.nuitparser.service.reader;

import com.ram.nuitparser.service.ParsedTelexHolder;
import com.ram.nuitparser.service.TelexParserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class LogFileReaderService {

    private final TelexParserService telexParserService;
    private final ParsedTelexHolder parsedTelexHolder;

    // Add ParsedTelexHolder dependency
    public LogFileReaderService(
            TelexParserService telexParserService,
            ParsedTelexHolder parsedTelexHolder
    ) {
        this.telexParserService = telexParserService;
        this.parsedTelexHolder = parsedTelexHolder;
    }

    @PostConstruct
    public void readTelexLog() {
        try (InputStream inputStream = getClass().getResourceAsStream("/logs/telex.log")) {
            if (inputStream == null) {
                System.err.println("Telex log file not found in /logs/telex.log");
                return;
            }

            // Read entire file at once for simplicity
            String rawTelex = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();

            if (rawTelex.isEmpty()) {
                System.err.println("Telex log file is empty");
                return;
            }

            // Store raw telex immediately
            parsedTelexHolder.store(null, rawTelex);

            // Process through pipeline
            telexParserService.parse(rawTelex);

        } catch (Exception e) {
            System.err.println("Critical error reading telex log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}