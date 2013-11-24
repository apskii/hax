package com.github.apsk.hax;

import com.github.apsk.hax.parsers.Parser1;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

public class HAX {
    public static Parser1<?> elem(String name) {
        return r -> {
            for (;;) {
                XMLEvent event = r.peek();
                if (event.isStartElement()) {
                    QName qname = event.asStartElement().getName();
                    if (qname.getLocalPart().equals(name))
                        return null;
                }
                r.nextEvent();
            }
        };
    }
    public static Parser1<?> elemQ(QName qname) {
        return r -> {
            for (;;) {
                XMLEvent event = r.peek();
                if (event.isStartElement()) {
                    QName tqname = event.asStartElement().getName();
                    if (tqname.equals(qname))
                        return null;
                }
                r.nextEvent();
            }
        };
    }
    public static Parser1<String> text() {
        return r -> {
            r.nextEvent();
            return r.getElementText();
        };
    }
    public static Parser1<String> chars() {
        return r -> {
            StringBuilder stringBuilder = new StringBuilder();
            while (!r.peek().isCharacters())
                r.nextEvent();
            for (;;) {
                XMLEvent event = r.peek();
                if (event.isCharacters()) {
                    stringBuilder.append(event.asCharacters().getData());
                    r.nextEvent();
                } else {
                    return stringBuilder.toString();
                }
            }
        };
    }
    public static Parser1<String> attr(String name) {
        return r -> {
            XMLEvent event = r.peek();
            Iterator<Attribute> iterator = event.asStartElement().getAttributes();
            while (iterator.hasNext()) {
                Attribute attr = iterator.next();
                if (attr.getName().getLocalPart().equals(name))
                    return attr.getValue();
            }
            return null;
        };
    }
    public static Parser1<String> attrQ(QName qname) {
        return r -> {
            XMLEvent event = r.peek();
            Iterator<Attribute> iterator = event.asStartElement().getAttributes();
            while (iterator.hasNext()) {
                Attribute attr = iterator.next();
                if (attr.getName().equals(qname))
                    return attr.getValue();
            }
            return null;
        };
    }
}
