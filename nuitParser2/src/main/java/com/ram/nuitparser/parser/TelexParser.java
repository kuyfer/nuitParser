
package com.ram.nuitparser.parser;

public interface TelexParser<T> {
    T parse(String body, String sender, String receivers);
}
