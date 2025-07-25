package com.ram.nuitparser.service;

import com.ram.nuitparser.enums.TelexType;
import com.ram.nuitparser.parser.TelexRouter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelexParserService {

    private final TelexRouter telexRouter;

    public TelexParserService(TelexRouter telexRouter) {
        this.telexRouter = telexRouter;
    }

    public void parse(String rawTelex) {
        List<String> lines = rawTelex.lines()
                .filter(line -> !line.trim().isEmpty())
                .toList();

        if (lines.size() < 3) {
            System.err.println("Telex message too short to parse.");
            return;
        }

        String sender = lines.get(0).trim();
        String receivers = lines.get(1).trim();
        String firstBodyLine = lines.get(2).trim();
        String messageBody = String.join("\n", lines.subList(2, lines.size()));

        TelexType telexType = detectType(firstBodyLine);

        telexRouter.route(messageBody, telexType, sender, receivers);
    }

    private TelexType detectType(String line) {
        if (line.toUpperCase().contains("ASM")) return TelexType.ASM;
        if (line.toUpperCase().contains("SSM")) return TelexType.SSM;
        if (line.toUpperCase().contains("MVT")) return TelexType.MVT;
        return TelexType.UNKNOWN;
    }
}
