package com.github.apsk.hax.parser;

import javax.xml.stream.XMLStreamException;

public class ParserException extends XMLStreamException {
    public ParserException(String msg) {
        super(msg);
    }
}
