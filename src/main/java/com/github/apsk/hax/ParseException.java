package com.github.apsk.hax;

import javax.xml.stream.XMLStreamException;

public class ParseException extends XMLStreamException {
    public ParseException(String msg) {
        super(msg);
    }
}
