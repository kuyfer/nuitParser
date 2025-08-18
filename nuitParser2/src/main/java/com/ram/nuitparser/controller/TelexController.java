package com.ram.nuitparser.controller;

import com.ram.nuitparser.model.telex.TelexMessage;
import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.service.ParsedTelexHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telex")
@CrossOrigin(origins = "http://localhost:5173")
public class TelexController {
    private static final Logger logger = LoggerFactory.getLogger(TelexController.class);

    private final ParsedTelexHolder holder;

    public TelexController(ParsedTelexHolder holder) {
        this.holder = holder;
        logger.info("TelexController initialized");
    }

    @GetMapping
    public ResponseEntity<?> getTelex() {
        logger.info("Received request for telex data");

        // Get the parsed message
        TelexMessage message = holder.getTelexMessage();
        String rawTelex = holder.getRawTelex();

        // Handle case where no telex has been processed
        if (message == null && rawTelex == null) {
            logger.warn("No telex data available - returning 204");
            return ResponseEntity.noContent().build();
        }

        logger.debug("Building telex response");
        TelexResponse response = new TelexResponse(
                rawTelex,
                message
        );

        logger.info("Returning telex data");
        return ResponseEntity.ok(response);
    }

    // Response DTO
    public static record TelexResponse(
            String raw,
           TelexMessage parsed
    ) {}
}