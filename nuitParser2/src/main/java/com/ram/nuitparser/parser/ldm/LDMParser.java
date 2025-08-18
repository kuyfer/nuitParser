package com.ram.nuitparser.parser.ldm;


import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.model.telex.ldm.LdmMessage;
import com.ram.nuitparser.parser.TelexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LDMParser implements TelexParser<LdmMessage> {
    private static final Logger logger = LoggerFactory.getLogger(LDMParser.class);


    public LdmMessage parse(String body, String sender, String receivers) {
        logger.info("Starting LDM message parsing");
        logger.debug("Sender: {}, Receivers: {}", sender, receivers);

        if (body == null || body.isBlank()) {
            logger.warn("Empty telex body received - skipping parsing");
            return null;
        }

        LdmMessage message = new LdmMessage();
        return message;
    }


}
