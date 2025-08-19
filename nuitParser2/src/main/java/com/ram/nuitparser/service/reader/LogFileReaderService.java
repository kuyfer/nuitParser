package com.ram.nuitparser.service.reader;

import com.ram.nuitparser.service.ParsedTelexHolder;
import com.ram.nuitparser.service.TelexParserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class LogFileReaderService {
    private static final Logger logger = LoggerFactory.getLogger(LogFileReaderService.class);

    private final TelexParserService telexParserService;
    private final ParsedTelexHolder parsedTelexHolder;

    public LogFileReaderService(
            TelexParserService telexParserService,
            ParsedTelexHolder parsedTelexHolder
    ) {
        this.telexParserService = telexParserService;
        this.parsedTelexHolder = parsedTelexHolder;
        logger.info("LogFileReaderService initialized");
    }

    @PostConstruct
    public void readTelexLog() {
        logger.info("Starting telex log processing");
        try (InputStream inputStream = getClass().getResourceAsStream("/logs/telex.log")) {
            if (inputStream == null) {
                logger.error("Telex log file not found in /logs/telex.log");
                return;
            }

            logger.debug("Reading telex log file");
            String rawLogContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();

            if (rawLogContent.isEmpty()) {
                logger.warn("Telex log file is empty");
                return;
            }

            // Split the log into individual telex messages using double newline separator
            String[] telexMessages = rawLogContent.split("\\n\\s*\\n");
            logger.info("Found {} telex messages in log file", telexMessages.length);

            for (int i = 0; i < telexMessages.length; i++) {
                String rawTelex = telexMessages[i].trim();
                if (rawTelex.isEmpty()) {
                    logger.debug("Skipping empty telex message");
                    continue;
                }

                logger.info("Processing telex message {}/{} ({} chars)",
                        i + 1, telexMessages.length, rawTelex.length());

                // Process each telex through the pipeline
                telexParserService.parse(rawTelex);

                // Optional: Add delay between processing if needed
                // Thread.sleep(100);
            }

            logger.info("Completed processing {} telex messages", telexMessages.length);

        } catch (Exception e) {
            logger.error("Critical error reading telex log: {}", e.getMessage(), e);
        }
    }
}