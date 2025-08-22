package com.ram.nuitparser.parser;

import com.ram.nuitparser.enums.TelexType;
import com.ram.nuitparser.model.telex.TelexMessage;
import com.ram.nuitparser.parser.asm.ASMParser;
import com.ram.nuitparser.parser.ldm.LDMParser;
import com.ram.nuitparser.parser.mvt.MVTParser;
import com.ram.nuitparser.parser.ssm.SSMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelexRouter {
    private static final Logger logger = LoggerFactory.getLogger(TelexRouter.class);

    private final ASMParser asmParser;
    private final MVTParser mvtParser;
    private final SSMParser ssmParser;
    private final LDMParser ldmParser;

    public TelexRouter(ASMParser asmParser, MVTParser mvtParser, SSMParser ssmParser, LDMParser ldmParser) {
        this.asmParser = asmParser;
        this.mvtParser = mvtParser;
        this.ssmParser = ssmParser;
        this.ldmParser = ldmParser;
        logger.info("TelexRouter initialized with 4 parsers");
    }

    public TelexMessage route(String body, TelexType type,
                              String priority, String destination, String origin,
                              String msgId, String header, String dblSig, String smi) {
        logger.info("Getting parser for type: {}", type);
        switch (type) {
            case ASM -> {
                logger.debug("Returning ASM parser");
                return asmParser.parse(body, priority, destination, origin, msgId, header, dblSig, smi);
            }
            case SSM -> {
                logger.debug("Returning SSM parser");
                return ssmParser.parse(body, priority, destination, origin, msgId, header, dblSig, smi);
            }
            case MVT -> {
                logger.debug("Returning MVT parser");
                return mvtParser.parse(body, priority, destination, origin, msgId, header, dblSig, smi);
            }
            case LDM -> {
                logger.debug("Returning LDM parser");
                return ldmParser.parse(body, priority, destination, origin, msgId, header, dblSig, smi);
            }
            default -> {
                logger.error("No parser available for type: {}", type);
                return null;
            }
        }
    }
}