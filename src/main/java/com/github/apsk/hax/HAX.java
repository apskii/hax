package com.github.apsk.hax;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.hax.parser.ParserException;
import com.github.apsk.hax.parser.arity.Parser1;
import com.github.apsk.hax.parser.arity.Parser2;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HAX {
    public static Parser1<?> skipSpaces = r -> {
        XMLEvent event = r.cur();
        while (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
            event = r.next();
        }
        return null;
    };

    public static Parser1<?> skipAttrs = r -> {
        XMLEvent event = r.cur();
        // event.is
        while (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
            event = r.next();
        }
        return null;
    };

    public static Parser1<?> step = r -> {
        r.next();
        skipSpaces.run(r);
        return null;
    };

    public static Parser1<?> skipTo(QName name) {
        return r -> {
            for (;;) {
                XMLEvent event = r.cur();
                if (event.isStartElement()) {
                    QName eventElemName = event.asStartElement().getName();
                    if (eventElemName.equals(name))
                        return null;
                }
                if (!r.hasNext()) {
                    throw new ParserException(
                        "`skipTo(" + name + ")` reached the end of stream."
                    );
                }
                r.next();
            }
        };
    }

    public static Parser1<?> skipTo(String name) {
        return skipTo(new QName(name));
    }

    public static Parser1<?> open(QName name) {
        return r -> {
            XMLEvent event = r.cur();
            if (event.isStartElement()) {
                QName eventElemName = event.asStartElement().getName();
                if (eventElemName.equals(name)) {
                    r.next();
                    skipSpaces.run(r);
                    return null;
                } else {
                    throw new ParserException(
                        "`open(" + name + ")` called on wrong opening element."
                    );
                }
            } else {
                throw new ParserException(
                    "`open(" + name + ")` called on non-opening element."
                );
            }
        };
    }

    public static Parser1<?> open(String name) {
        return open(new QName(name));
    }

    public static Parser1<?> opens(QName name) {
        return r -> {
            XMLEvent event = r.cur();
            if (event.isStartElement()) {
                QName eventElemName = event.asStartElement().getName();
                if (eventElemName.equals(name)) {
                    return null;
                } else {
                    throw new ParserException(
                        "`opens(" + name + ")` called on wrong opening element."
                    );
                }
            } else {
                throw new ParserException(
                    "`opens(" + name + ")` called on non-opening element."
                );
            }
        };
    }

    public static Parser1<?> opens(String name) {
        return opens(new QName(name));
    }

    public static Parser1<?> close(QName name) {
        return r -> {
            XMLEvent event = r.cur();
            if (event.isEndElement()) {
                QName eventElemName = event.asEndElement().getName();
                if (eventElemName.equals(name)) {
                    r.next();
                    return null;
                } else {
                    throw new ParserException(
                        "`close(" + name + ")` called on wrong closing element."
                    );
                }
            } else {
                throw new ParserException(
                    "`close(" + name + ")` called on non-closing element."
                );
            }
        };
    }

    public static Parser1<?> close(String name) {
        return close(new QName(name));
    }

    public static Parser1<Boolean> tryClose(QName name) {
        return r -> {
            for (;;) {
                XMLEvent event = r.cur();
                if (event.isEndElement()) {
                    QName eventElemName = event.asEndElement().getName();
                    if (eventElemName.equals(name)) {
                        r.next();
                        return true;
                    }
                } else if (!event.isCharacters() || !event.asCharacters().isWhiteSpace()) {
                    return false;
                }
                if (!r.hasNext()) {
                    throw new ParserException(
                        "`tryClose(" + name + ")` called at the end of stream."
                    );
                }
                r.next();
            }
        };
    }

    public static Parser1<Boolean> tryClose(String name) {
        return tryClose(new QName(name));
    }

    public static Parser1<String> text = r -> {
        XMLEvent event = r.cur();
        if (!event.isCharacters()) {
            throw new ParserException("`text` called on non-characters data.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (event.isCharacters()) {
            stringBuilder.append(event.asCharacters().getData());
            if (r.hasNext()) {
                event = r.next();
            }
        }
        return stringBuilder.toString();
    };

    public static Parser1<String> attr(QName name) {
        return r -> {
            XMLEvent event = r.cur();
            if (!event.isStartElement()) {
                throw new ParserException(
                    "`attr(" + name + ")` called at non-opening element."
                );
            }
            return r.attr(name).getValue();
        };
    }

    public static Parser1<String> attr(String name) {
        return attr(new QName(name));
    }

    public static Parser1<Map<QName, String>> attrs = r -> {
        if (!r.cur().isStartElement()) {
            throw new ParserException("`attrs` called at non-opening element.");
        }
        Map<QName, String> attrs = new HashMap<>();
        Iterator<Attribute> iterator = r.attrs();
        while (iterator.hasNext()) {
            Attribute attr = iterator.next();
            attrs.put(attr.getName(), attr.getValue());
        }
        return attrs;
    };

    /////////////////////////////////////////////////////////////////////////////////////

    public static <X> Parser1<X> open(QName name, Parser<X> p) {
        return opens(name).nextR(p).nextL(step);
    }

    public static <X> Parser1<X> open(String name, Parser<X> p) {
        return opens(name).nextR(p).nextL(step);
    }

    public static Parser1<String> elemAttr(QName elemName, QName attrName) {
        return opens(elemName).nextR(attr(attrName)).nextL(step).nextL(close(elemName));
    }

    public static Parser1<String> elemAttr(String elemName, String attrName) {
        return opens(elemName).nextR(attr(attrName)).nextL(step).nextL(close(elemName));
    }

    public static Parser1<Map<QName, String>> elemAttrs(QName name) {
        return opens(name).nextR(attrs).nextL(step).nextL(close(name));
    }

    public static Parser1<Map<QName, String>> elemAttrs(String name) {
        return elemAttrs(new QName(name));
    }

    public static Parser1<String> elemText(QName name) {
        return open(name).nextR(text).nextL(close(name));
    }

    public static Parser1<String> elemText(String name) {
        return elemText(new QName(name));
    }

    public static Parser2<String,String> elemAttrAndText(QName elemName, QName attrName) {
        return opens(elemName).nextR(attr(attrName)).and(text).nextL(close(elemName));
    }

    public static Parser2<String,String> elemAttrAndText(String elemName, String attrName) {
        return elemAttrAndText(new QName(elemName), new QName(attrName));
    }

    public static Parser2<Map<QName,String>,String> elemAttrsAndText(QName name) {
        return opens(name).nextR(attrs).and(text).nextL(close(name));
    }

    public static Parser2<Map<QName,String>,String> elemAttrsAndText(String name) {
        return elemAttrsAndText(new QName(name));
    }

    public static <X> Parser1<X> within(QName name, Parser<X> p) {
        return open(name).nextR(p).nextL(close(name))::run;
    }

    public static <X> Parser1<X> within(String name, Parser<X> p) {
        return within(new QName(name), p);
    }

    public static <X,Y> Parser2<X,Y> within(QName name, Parser<X> t, Parser<Y> p) {
        return opens(name).nextR(t.asParser1()).nextL(step).and(p).nextL(close(name));
    }

    public static <X,Y> Parser2<X,Y> within(String name, Parser<X> t, Parser<Y> p) {
        return within(new QName(name), t, p);
    }

    public static <X> Parser1<List<X>> manyWithin(QName name, Parser<X> p) {
        return open(name).nextR(p.until(tryClose(name)));
    }

    public static <X> Parser1<List<X>> manyWithin(String name, Parser<X> p) {
        return manyWithin(new QName(name), p);
    }

    public static <X,Y> Parser2<X,List<Y>> manyWithin(QName name, Parser<X> t, Parser<Y> p) {
        return opens(name).nextR(t.asParser1()).nextL(step).and(p.until(tryClose(name)));
    }

    public static <X,Y> Parser2<X,List<Y>> manyWithin(String name, Parser<X> t, Parser<Y> p) {
        return manyWithin(new QName(name), t, p);
    }
}
