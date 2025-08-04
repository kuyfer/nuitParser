package com.ram.nuitparser.service;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParsedTelexHolder {
    private static final Logger logger = LoggerFactory.getLogger(ParsedTelexHolder.class);

    private AsmMessage asmMessage;
    private String rawTelex;

    public void store(AsmMessage message, String raw) {
        logger.info("Storing parsed telex in holder");
        logger.debug("Message: {}, Raw length: {}",
                message != null ? message.getFlightDesignator() : "null",
                raw != null ? raw.length() : 0);

        this.asmMessage = message;
        this.rawTelex = raw;
    }

    public AsmMessage getAsmMessage() {
        logger.debug("Retrieving parsed telex from holder");
        return asmMessage;
    }

    public String getRawTelex() {
        logger.debug("Retrieving raw telex from holder");
        return rawTelex;
    }
}