package com.ram.nuitparser.service.reader;

import com.ram.nuitparser.service.TelexParserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class LogFileReaderService {

    private final TelexParserService telexParserService;

    public LogFileReaderService(TelexParserService telexParserService) {
        this.telexParserService = telexParserService;
    }

    @PostConstruct
    public void readTelexLog() {
        try (InputStream inputStream = getClass().getResourceAsStream("/logs/telex.log")) {
            if (inputStream == null) {
                System.err.println("Telex log file not found.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder telexBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    telexBuilder.append(line).append("\n");
                }

                String rawTelex = telexBuilder.toString().trim();
                if (!rawTelex.isEmpty()) {
                    telexParserService.parse(rawTelex);
                } else {
                    System.err.println("Telex log file is empty.");
                }
            }

        } catch (Exception e) {
            System.err.println("Error reading telex log file: " + e.getMessage());
        }
    }
}
