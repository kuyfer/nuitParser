package com.ram.nuitparser.service.reader;

import com.ram.nuitparser.service.ParsedTelexHolder;
import com.ram.nuitparser.service.TelexParserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
            String rawTelex = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();

            if (rawTelex.isEmpty()) {
                logger.warn("Telex log file is empty");
                return;
            }

            logger.info("Storing raw telex ({} characters)", rawTelex.length());
            parsedTelexHolder.store(null, rawTelex);

            logger.info("Processing telex through pipeline");
            telexParserService.parse(rawTelex);
            logger.info("Telex processing completed");

        } catch (Exception e) {
            logger.error("Critical error reading telex log: {}", e.getMessage(), e);
        }
    }
}