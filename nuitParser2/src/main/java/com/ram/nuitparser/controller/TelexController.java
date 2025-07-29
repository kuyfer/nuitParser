package com.ram.nuitparser.controller;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.service.ParsedTelexHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telex")
public class TelexController {

    private final ParsedTelexHolder holder;

    public TelexController(ParsedTelexHolder holder) {
        this.holder = holder;
    }

    @GetMapping
    public ResponseEntity<?> getTelex() {
        // Get the parsed message
        AsmMessage message = holder.getAsmMessage();
        String rawTelex = holder.getRawTelex();

        // Handle case where no telex has been processed
        if (message == null && rawTelex == null) {
            return ResponseEntity.noContent().build();
        }

        // Create response object
        TelexResponse response = new TelexResponse(
                rawTelex,
                message
        );

        return ResponseEntity.ok(response);
    }

    // Response DTO
    public static record TelexResponse(
            String raw,
            AsmMessage parsed
    ) {}
}