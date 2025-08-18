package com.ram.nuitparser.parser.ssm;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.model.telex.ssm.SsmMessage;
import com.ram.nuitparser.parser.TelexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SSMParser implements TelexParser<SsmMessage> {
    private static final Logger logger = LoggerFactory.getLogger(SSMParser.class);


    public SsmMessage parse(String body, String sender, String receivers) {
        logger.info("Starting SSM message parsing");
        logger.debug("Sender: {}, Receivers: {}", sender, receivers);

        if (body == null || body.isBlank()) {
            logger.warn("Empty telex body received - skipping parsing");
            return null;
        }

        SsmMessage message = new SsmMessage();
        return message;
    }
}
