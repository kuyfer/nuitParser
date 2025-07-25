package com.ram.nuitparser.parser;

import com.ram.nuitparser.enums.TelexType;
import com.ram.nuitparser.parser.asm.ASMParser;
import org.springframework.stereotype.Component;

@Component
public class TelexRouter {

    private final ASMParser asmParser;

    public TelexRouter(ASMParser asmParser) {
        this.asmParser = asmParser;
    }

    public void route(String messageBody, TelexType type, String sender, String receivers) {
        switch (type) {
            case ASM -> {
                asmParser.parse(messageBody, sender, receivers);
            }
            case SSM, MVT, UNKNOWN -> {
                System.err.println("Unsupported or unimplemented telex type: " + type);
            }
        }
    }
}
