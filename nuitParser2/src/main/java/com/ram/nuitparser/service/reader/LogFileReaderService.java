package com.ram.nuitparser.service.reader;

import com.ram.nuitparser.service.TelexParserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LogFileReaderService {
    private static final Logger logger = LoggerFactory.getLogger(LogFileReaderService.class);

    private final TelexParserService telexParserService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private WatchService watchService;
    private boolean watching = false;

    @Value("${telex.directory.path:telex_files}")
    private String telexDirectoryPath;

    public LogFileReaderService(TelexParserService telexParserService) {
        this.telexParserService = telexParserService;
        logger.info("LogFileReaderService initialized");
    }

    @PostConstruct
    public void init() {
        startWatching();
        logger.info("Started directory monitoring for telex files using WatchService");
    }

    @PreDestroy
    public void cleanup() {
        stopWatching();
        executorService.shutdown();
        logger.info("Stopped directory monitoring and shutdown executor service");
    }

    private void startWatching() {
        Path dir = Paths.get(telexDirectoryPath);

        // Create directory if it doesn't exist
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
                logger.info("Created telex directory: {}", telexDirectoryPath);
            } catch (IOException e) {
                logger.error("Failed to create telex directory: {}", telexDirectoryPath, e);
                return;
            }
        }

        if (!Files.isDirectory(dir)) {
            logger.warn("Telex path is not a directory: {}", telexDirectoryPath);
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            watching = true;
            executorService.submit(this::watchDirectory);

        } catch (IOException e) {
            logger.error("Error setting up WatchService for directory: {}", telexDirectoryPath, e);
        }
    }

    private void stopWatching() {
        watching = false;
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                logger.error("Error closing WatchService", e);
            }
        }
    }

    private void watchDirectory() {
        try {
            while (watching) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // Handle overflow
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        logger.warn("WatchService overflow occurred");
                        continue;
                    }

                    // Process only ENTRY_CREATE events
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();
                        Path fullPath = Paths.get(telexDirectoryPath).resolve(filename);

                        // Skip directories and hidden files
                        if (!Files.isDirectory(fullPath) && !filename.toString().startsWith(".")) {
                            processFile(fullPath);
                        }
                    }
                }

                // Reset the key to receive further events
                boolean valid = key.reset();
                if (!valid) {
                    logger.warn("WatchKey is no longer valid");
                    break;
                }
            }
        } catch (InterruptedException e) {
            logger.info("WatchService thread interrupted");
            Thread.currentThread().interrupt();
        } catch (ClosedWatchServiceException e) {
            logger.info("WatchService closed normally");
        } catch (Exception e) {
            logger.error("Error in WatchService", e);
        }
    }

    private void processFile(Path file) {
        try {
            // Small delay to ensure file is fully written
            Thread.sleep(100);

            String content = Files.readString(file);
            telexParserService.parse(content);
            logger.info("Processed new file: {}", file.getFileName());
        } catch (IOException e) {
            logger.error("Error reading file {}: {}", file.getFileName(), e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Processing interrupted for file: {}", file.getFileName());
        }
    }
}