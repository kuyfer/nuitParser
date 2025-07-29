package com.ram.nuitparser.parser;

import com.ram.nuitparser.enums.TelexType;
import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.parser.asm.ASMParser;
import org.springframework.stereotype.Component;

@Component
public class TelexRouter {

    private final ASMParser asmParser;

    public TelexRouter(ASMParser asmParser) {
        this.asmParser = asmParser;
    }

    public AsmMessage route(String messageBody, TelexType type, String sender, String receivers) {
        switch (type) {
            case ASM -> {
                return asmParser.parse(messageBody, sender, receivers);
            }
            case SSM, MVT, UNKNOWN -> {
                System.err.println("Unsupported or unimplemented telex type: " + type);
                return null;
            }
            default -> {
                System.err.println("Unknown telex type encountered: " + type);
                return null;
            }
        }
    }
}