package com.ram.nuitparser.parser;

public interface TelexParser<T> {
    T parse(String body, String sender, String receivers,
            String priority, String destination, String origin,
            String msgId, String header, String dblSig, String smi);
}