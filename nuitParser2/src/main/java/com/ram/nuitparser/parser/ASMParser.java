package com.ram.nuitparser.parser;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import org.springframework.stereotype.Component;

@Component
public class ASMParser {

    public AsmMessage parse(String body, String sender, String receivers) {
        AsmMessage message = new AsmMessage();

        message.setRawTelex(body);
        message.setSender(sender);
        message.setReceivers(receivers);

        System.out.println("Parsed ASM Message:");
        System.out.println("Sender: " + sender);
        System.out.println("Receivers: " + receivers);
        System.out.println("Body:\n" + body);

        return message;
    }
}
