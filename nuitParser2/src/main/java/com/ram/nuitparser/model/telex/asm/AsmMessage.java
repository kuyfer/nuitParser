package com.ram.nuitparser.model.telex.asm;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AsmMessage {

    private String sender;      // First line of the telex
    private String receivers;   // Second line of the telex
    private String rawTelex;    // Body (from line 3 onwards)

    @Override
    public String toString() {
        return "AsmMessage{" +
                "sender='" + sender + '\'' +
                ", receivers='" + receivers + '\'' +
                ", rawTelex='" + rawTelex + '\'' +
                '}';
    }
}
