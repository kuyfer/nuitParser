package com.ram.nuitparser.service;

import com.ram.nuitparser.model.telex.TelexMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ParsedTelexHolder {
    private static final Logger logger = LoggerFactory.getLogger(ParsedTelexHolder.class);

    private final List<TelexMessage> telexMessages = new ArrayList<>();
    private final List<String> rawTelexes = new ArrayList<>();

    public void store(TelexMessage message, String raw) {  // Updated parameter type
        logger.info("Storing parsed telex in holder");
        logger.debug("Message type: {}, Raw length: {}",
                message != null ? message.getType() : "null",
                raw != null ? raw.length() : 0);

        telexMessages.add(message);
        rawTelexes.add(raw);
    }

    public List<TelexMessage> getAllTelexMessages() {  // Renamed method for clarity
        logger.debug("Retrieving parsed telex from holder");
        return Collections.unmodifiableList(telexMessages);
    }

    public List<String> getAllRawTelexes() {
        logger.debug("Retrieving raw telex from holder");
        return Collections.unmodifiableList(rawTelexes);

    }
}