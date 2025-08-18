package com.ram.nuitparser.service;

import com.ram.nuitparser.model.telex.TelexMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParsedTelexHolder {
    private static final Logger logger = LoggerFactory.getLogger(ParsedTelexHolder.class);

    private TelexMessage telexMessage;  // Changed from AsmMessage to TelexMessage
    private String rawTelex;

    public void store(TelexMessage message, String raw) {  // Updated parameter type
        logger.info("Storing parsed telex in holder");
        logger.debug("Message type: {}, Raw length: {}",
                message != null ? message.getType() : "null",
                raw != null ? raw.length() : 0);

        this.telexMessage = message;
        this.rawTelex = raw;
    }

    public TelexMessage getTelexMessage() {  // Renamed method for clarity
        logger.debug("Retrieving parsed telex from holder");
        return telexMessage;
    }

    public String getRawTelex() {
        logger.debug("Retrieving raw telex from holder");
        return rawTelex;
    }
}