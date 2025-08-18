package com.ram.nuitparser.parser.mvt;

import com.ram.nuitparser.enums.MovementType;
import com.ram.nuitparser.model.telex.asm.AsmMessage;
import com.ram.nuitparser.model.telex.mvt.MvtMessage;
import com.ram.nuitparser.parser.TelexParser;
import com.ram.nuitparser.parser.asm.ASMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MVTParser implements TelexParser<MvtMessage> {
    private static final Logger logger = LoggerFactory.getLogger(MVTParser.class);

    public MvtMessage parse(String body, String sender, String receivers) {
        logger.info("Starting MVT message parsing");
        logger.debug("Sender: {}, Receivers: {}", sender, receivers);

        if (body == null || body.isBlank()) {
            logger.warn("Empty telex body received - skipping parsing");
            return null;
        }

        MvtMessage message = new MvtMessage();
        return message;
    }
}
