package com.github.apsk.hax;

import com.github.apsk.hax.parsers.Parser1;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import java.util.List;

public class HAX {
    public static Parser1<?> open(String name) {
        return r -> {
            for (;;) {
                if (!r.hasNext()) {
                    throw new ParseException(
                        "`open(" + name + ")` reached the end of stream."
                    );
                }
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

    public static Parser1<?> openQ(QName qname) {
        return r -> {
            for (;;) {
                if (!r.hasNext()) {
                    throw new ParseException(
                        "`openQ(" + qname + ")` reached the end of stream."
                    );
                }
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

    public static Parser1<?> close(String name) {
        return r -> {
            if (!r.hasNext()) {
                throw new ParseException(
                    "`close(" + name + ")` called at the end of stream."
                );
            }
            XMLEvent event = r.peek();
            if (event.isEndElement()) {
                QName qname = event.asEndElement().getName();
                if (qname.getLocalPart().equals(name)) {
                    r.nextEvent();
                    return null;
                } else {
                    throw new ParseException(
                        "`close(" + name + ")` called on wrong closing element."
                    );
                }
            } else {
                throw new ParseException(
                    "`close(" + name + ")` called on non-closing element."
                );
            }
        };
    }

    public static Parser1<Boolean> closing(String name) {
        return r -> {
            for (;;) {
                if (!r.hasNext()) {
                    throw new ParseException(
                        "`closing(" + name + ")` called at the end of stream."
                    );
                }
                XMLEvent event = r.peek();
                if (event.isEndElement()) {
                    QName qname = event.asEndElement().getName();
                    if (qname.getLocalPart().equals(name)) {
                        return true;
                    }
                } else if (!event.isCharacters() || !event.asCharacters().isWhiteSpace()) {
                    return false;
                }
                r.nextEvent();
            }
        };
    }

    public static Parser1<Boolean> closingQ(QName qname) {
        return r -> {
            if (!r.hasNext()) {
                throw new ParseException(
                    "`closingQ(" + qname + ")` called at the end of stream."
                );
            }
            XMLEvent event = r.peek();
            if (event.isEndElement()) {
                QName tqname = event.asEndElement().getName();
                if (tqname.equals(qname))
                    return true;
            }
            return false;
        };
    }

    public static Parser1<String> text() {
        return r -> {
            if (!r.hasNext()) {
                throw new ParseException(
                    "`text` called at the end of stream."
                );
            }
            r.nextEvent();
            try {
                return r.getElementText();
            } catch (XMLStreamException ignored) {
                throw new ParseException(
                    "`text` called on non text-only element."
                );
            }
        };
    }

    public static Parser1<String> chars() {
        return r -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (;;) {
                if (!r.hasNext()) {
                    throw new ParseException(
                        "`chars` reached the end of stream."
                    );
                }
                if (r.peek().isCharacters()) break;
                r.nextEvent();
            }
            for (;;) {
                XMLEvent event = r.peek();
                if (event.isCharacters()) {
                    stringBuilder.append(event.asCharacters().getData());
                    if (r.hasNext()) {
                        r.nextEvent();
                        continue;
                    }
                }
                return stringBuilder.toString();
            }
        };
    }

    public static Parser1<String> attr(String name) {
        return r -> {
            if (!r.hasNext()) {
                throw new ParseException(
                    "`attr(" + name + ")` called at the end of stream."
                );
            }
            XMLEvent event = r.peek();
            if (!event.isStartElement()) {
                throw new ParseException(
                    "`attr(" + name + ")` called at non-opening element."
                );
            }
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
            if (!r.hasNext()) {
                throw new ParseException(
                    "`attrQ(" + qname + ")` called at the end of stream."
                );
            }
            XMLEvent event = r.peek();
            if (!event.isStartElement()) {
                throw new ParseException(
                    "`attrQ(" + qname + ")` called at non-opening element."
                );
            }
            Iterator<Attribute> iterator = event.asStartElement().getAttributes();
            while (iterator.hasNext()) {
                Attribute attr = iterator.next();
                if (attr.getName().equals(qname))
                    return attr.getValue();
            }
            return null;
        };
    }

    /////////////////////////////////////////////////////////////////////////////////////

    public static Parser1<String> elemText(String name) {
        return open(name).nextR(text());
    }

    public static Parser1<String> elemTextQ(QName qname) {
        return openQ(qname).nextR(text());
    }

    public static <X> Parser<List<X>> manyWithin(String name, Parser<X> p) {
        return open(name).nextR(p.until(closing(name)));
    }

    public static <X> Parser<List<X>> manyWithinQ(QName qname, Parser<X> p) {
        return openQ(qname).nextR(p.until(closingQ(qname)));
    }
}
