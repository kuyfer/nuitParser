package com.ram.nuitparser.service;

import com.ram.nuitparser.model.telex.asm.AsmMessage;
import org.springframework.stereotype.Service;

@Service
public class ParsedTelexHolder {
    private AsmMessage asmMessage;
    private String rawTelex;

    public void store(AsmMessage message, String raw) {  // Changed method name to 'store'
        this.asmMessage = message;
        this.rawTelex = raw;
    }

    public AsmMessage getAsmMessage() {
        return asmMessage;
    }

    public String getRawTelex() {
        return rawTelex;
    }
}