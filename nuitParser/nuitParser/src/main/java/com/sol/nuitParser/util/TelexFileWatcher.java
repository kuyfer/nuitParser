package com.sol.nuitParser.util;

import com.sol.nuitParser.service.TelexParserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;

@Component
public class TelexFileWatcher {

    private final TelexParserService telexParserService;

    @Value("${telex.log.path}")
    private String logFilePath;

    public TelexFileWatcher(TelexParserService telexParserService) {
        this.telexParserService = telexParserService;
    }

    @PostConstruct
    public void startWatching() {
        new Thread(this::watchLoop).start();
    }

    private void watchLoop() {
        try (RandomAccessFile reader = new RandomAccessFile(logFilePath, "r")) {
            long filePointer = 0;

            while (true) {
                long fileLength = new File(logFilePath).length();

                if (fileLength < filePointer) {
                    // Log file was reset
                    filePointer = 0;
                }

                if (fileLength > filePointer) {
                    reader.seek(filePointer);
                    String line;
                    StringBuilder telexBuffer = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        // Assuming an empty line marks end of a telex
                        if (line.trim().isEmpty()) {
                            if (!telexBuffer.isEmpty()) {
                                telexParserService.processTelex(telexBuffer.toString());
                                telexBuffer.setLength(0); // Reset buffer
                            }
                        } else {
                            telexBuffer.append(line).append("\n");
                        }
                    }
                    filePointer = reader.getFilePointer();
                }

                Thread.sleep(1000); // Delay for polling
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
