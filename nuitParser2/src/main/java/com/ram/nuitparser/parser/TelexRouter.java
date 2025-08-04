package com.ram.nuitparser.parser;

import com.ram.nuitparser.enums.TelexType;
import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.parser.asm.ASMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelexRouter {
    private static final Logger logger = LoggerFactory.getLogger(TelexRouter.class);

    private final ASMParser asmParser;

    public TelexRouter(ASMParser asmParser) {
        this.asmParser = asmParser;
        logger.info("TelexRouter initialized");
    }

    public AsmMessage route(String messageBody, TelexType type, String sender, String receivers) {
        logger.info("Routing telex of type: {}", type);
        switch (type) {
            case ASM -> {
                logger.debug("Routing to ASM parser");
                return asmParser.parse(messageBody, sender, receivers);
            }
            case SSM, MVT, UNKNOWN -> {
                logger.warn("Unsupported telex type: {}", type);
                return null;
            }
            default -> {
                logger.error("Unknown telex type encountered: {}", type);
                return null;
            }
        }
    }
}