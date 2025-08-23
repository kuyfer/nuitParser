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

    public void store(TelexMessage message, String raw) {
        logger.info("Storing parsed telex in holder");
        logger.debug("Message type: {}, Raw length: {}",
                message != null ? message.getType() : "null",
                raw != null ? raw.length() : 0);

        telexMessages.add(message);
        rawTelexes.add(raw);
    }

    public List<TelexMessage> getAllTelexMessages() {
        logger.debug("Retrieving parsed telex from holder");
        return Collections.unmodifiableList(telexMessages);
    }

    public List<String> getAllRawTelexes() {
        logger.debug("Retrieving raw telex from holder");
        return Collections.unmodifiableList(rawTelexes);
    }

    // New methods for pagination support
    public List<TelexMessage> getTelexMessages(int page, int size) {
        logger.debug("Retrieving paginated telex messages - page: {}, size: {}", page, size);
        return getPaginatedList(telexMessages, page, size);
    }

    public List<String> getRawTelexes(int page, int size) {
        logger.debug("Retrieving paginated raw telexes - page: {}, size: {}", page, size);
        return getPaginatedList(rawTelexes, page, size);
    }

    public long getTotalCount() {
        long count = telexMessages.size();
        logger.debug("Retrieving total telex count: {}", count);
        return count;
    }

    private <T> List<T> getPaginatedList(List<T> list, int page, int size) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        int fromIndex = page * size;
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(fromIndex + size, list.size());
        return new ArrayList<>(list.subList(fromIndex, toIndex));
    }
}