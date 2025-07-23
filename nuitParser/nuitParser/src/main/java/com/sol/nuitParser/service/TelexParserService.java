package com.sol.nuitParser.service;

import org.springframework.stereotype.Service;

@Service
public class TelexParserService {

    public void processTelex(String telexText) {
        System.out.println("Received telex:\n" + telexText);
        // Later: route and parse here
    }
}
