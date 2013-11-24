package com.github.apsk.hax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.util.function.Function;

@FunctionalInterface
public interface Parser<R> {
    R run(XMLEventReader eventReader) throws XMLStreamException;
    default <X> Parser<X> map(Function<R,X> f) {
        return r -> f.apply(this.run(r));
    }
}
