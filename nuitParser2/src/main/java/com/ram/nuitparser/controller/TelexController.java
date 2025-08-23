package com.ram.nuitparser.controller;

import com.ram.nuitparser.model.telex.TelexMessage;
import com.ram.nuitparser.service.ParsedTelexHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<?> getTelexes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        logger.info("Received request for telex data - page: {}, size: {}", page, size);

        // Get paginated messages and raw telexes
        List<TelexMessage> messages = holder.getTelexMessages(page, size);
        List<String> rawTelexes = holder.getRawTelexes(page, size);

        // Handle case where no telexes have been processed
        if (messages.isEmpty() && rawTelexes.isEmpty()) {
            logger.warn("No telex data available for page {} - returning 204", page);
            return ResponseEntity.noContent().build();
        }

        logger.debug("Building telex response with {} messages for page {}", messages.size(), page);
        List<TelexResponse> content = createResponseList(messages, rawTelexes);

        // Add pagination metadata
        long totalCount = holder.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        TelexPageResponse response = new TelexPageResponse(
                content, page, size, totalCount, totalPages
        );

        logger.info("Returning {} telex records for page {} of {}",
                content.size(), page, totalPages);
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
    }

    // Response DTO with pagination
    public record TelexPageResponse(
            List<TelexResponse> content,
            int currentPage,
            int pageSize,
            long totalElements,
            int totalPages
    ) {}

    // Individual telex response
    public record TelexResponse(
            String raw,
            TelexMessage parsed
    ) {}
}