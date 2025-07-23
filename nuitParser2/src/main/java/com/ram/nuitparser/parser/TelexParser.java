package com.ram.nuitparser.parser;

public interface TelexParser<T> {
    T parse(String rawTelex);
}
