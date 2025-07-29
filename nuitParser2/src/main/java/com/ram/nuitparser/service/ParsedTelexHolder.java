// 📍 com.ram.nuitparser.service.ParsedTelexHolder.java
package com.ram.nuitparser.service;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
@Service

public class ParsedTelexHolder {
    private AsmMessage asmMessage; // Strongly typed now
    private String rawTelex;

    public void storeTelex(AsmMessage message, String raw) {
        this.asmMessage = message;
        this.rawTelex = raw;
    }

    // Getters
    public AsmMessage getAsmMessage() { return asmMessage; }
    public String getRawTelex() { return rawTelex; }
}
