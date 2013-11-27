package com.github.apsk.hax.parser;

import com.github.apsk.hax.HAX;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.Iterator;

public final class HAXEventReader {
    final XMLEventReader reader;
    XMLEvent current;
    public HAXEventReader(InputStream inputStream) throws XMLStreamException {
        this.reader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
        this.current = reader.nextEvent();
    }
    public XMLEvent cur() {
        return current;
    }
    public XMLEvent peek() throws XMLStreamException {
        return reader.peek();
    }
    public XMLEvent next() throws XMLStreamException {
        current = reader.nextEvent();
        return current;
    }
    public boolean hasNext() {
        return reader.hasNext();
    }
    public Attribute attr(QName name) {
        return current.asStartElement().getAttributeByName(name);
    }
    public Iterator<Attribute> attrs() {
        return current.asStartElement().getAttributes();
    }
    public void skipTo(QName name) throws XMLStreamException {
        HAX.skipTo(name).run(this);
    }
    public void skipTo(String name) throws XMLStreamException {
        HAX.skipTo(new QName(name)).run(this);
    }
}
