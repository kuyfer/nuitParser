package com.ram.nuitparser.service.reader;

import com.ram.nuitparser.service.TelexParserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogFileReaderService {
    private static final Logger logger = LoggerFactory.getLogger(LogFileReaderService.class);

    private final TelexParserService telexParserService;

    @Value("${telex.directory.path:telex_files}")
    private String telexDirectoryPath;

    public LogFileReaderService(TelexParserService telexParserService) {
        this.telexParserService = telexParserService;
        logger.info("LogFileReaderService initialized");
    }

    @PostConstruct
    public void init() {
        processExistingFiles();
        logger.info("Started directory monitoring for telex files");
    }

    @Scheduled(fixedRate = 10000)
    public void checkForNewFiles() {
        Path dir = Paths.get(telexDirectoryPath);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            logger.warn("Telex directory does not exist: {}", telexDirectoryPath);
            return;
        }

        try {
            List<Path> files = Files.list(dir)
                    .filter(path -> !Files.isDirectory(path) && !path.getFileName().toString().startsWith("."))
                    .collect(Collectors.toList());

            for (Path file : files) {
                processFile(file);
            }
        } catch (IOException e) {
            logger.error("Error checking for new files: {}", e.getMessage(), e);
        }
    }

    private void processExistingFiles() {
        Path dir = Paths.get(telexDirectoryPath);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            logger.warn("Telex directory does not exist: {}", telexDirectoryPath);
            return;
        }

        try {
            List<Path> files = Files.list(dir)
                    .filter(path -> !Files.isDirectory(path) && !path.getFileName().toString().startsWith("."))
                    .collect(Collectors.toList());

            for (Path file : files) {
                processFile(file);
            }
        } catch (IOException e) {
            logger.error("Error processing existing files: {}", e.getMessage(), e);
        }
    }

    private void processFile(Path file) {
        try {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            telexParserService.parse(content); // Pass raw content only
        } catch (IOException e) {
            logger.error("Error reading file {}: {}", file.getFileName(), e.getMessage(), e);
        }
    }
}
