package com.ram.nuitparser.controller;

import com.ram.nuitparser.model.telex.TelexMessage;
import com.ram.nuitparser.service.ParsedTelexHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
    public ResponseEntity<?> getAllTelexes() {
        logger.info("Received request for all telex data");

        // Get all parsed messages and raw telexes
        List<TelexMessage> messages = holder.getAllTelexMessages();
        List<String> rawTelexes = holder.getAllRawTelexes();

        // Handle case where no telexes have been processed
        if (messages.isEmpty() && rawTelexes.isEmpty()) {
            logger.warn("No telex data available - returning 204");
            return ResponseEntity.noContent().build();
        }

        logger.debug("Building telex response with {} messages", messages.size());
        List<TelexResponse> response = createResponseList(messages, rawTelexes);

        logger.info("Returning {} telex records", response.size());
        return ResponseEntity.ok(response);
    }

    private List<TelexResponse> createResponseList(
            List<TelexMessage> messages,
            List<String> rawTelexes
    ) {
        // Ensure both lists have the same size
        int minSize = Math.min(messages.size(), rawTelexes.size());

        // Create a list of responses by iterating through the indices
        List<TelexResponse> responses = new ArrayList<>(minSize);
        for (int i = 0; i < minSize; i++) {
            responses.add(new TelexResponse(rawTelexes.get(i), messages.get(i)));
        }

        return responses;

        // Alternative using streams (more concise but less readable):
        // return IntStream.range(0, minSize)
        //         .mapToObj(i -> new TelexResponse(rawTelexes.get(i), messages.get(i)))
        //         .collect(Collectors.toList());
    }

    // Response DTO
    public static record TelexResponse(
            String raw,
            TelexMessage parsed
    ) {}
}