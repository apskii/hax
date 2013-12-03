package com.github.apsk.hax;

import com.github.apsk.hax.parser.*;
import com.github.apsk.hax.parser.arity.*;
import com.github.apsk.j8t.*;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class HAX {
    public static Parser1<?> skipSpaces = (reader, _pool) -> {
        XMLEvent event = reader.cur();
        while (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
            event = reader.next();
        }
        return null;
    };

    public static Parser1<?> step = (reader, _pool) -> {
        reader.next();
        skipSpaces.run(reader);
        return null;
    };

    public static Parser1<?> skipTo(QName name) {
        return (reader, _pool) -> {
            for (;;) {
                XMLEvent event = reader.cur();
                if (event.isStartElement()) {
                    QName eventElemName = event.asStartElement().getName();
                    if (eventElemName.equals(name))
                        return null;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                        "`skipTo(" + name + ")` reached the end of stream."
                    );
                }
                reader.next();
            }
        };
    }

    public static Parser1<?> skipTo(String name) {
        return skipTo(new QName(name));
    }

    public static Parser1<?> open(QName name) {
        return (reader, _pool) -> {
            XMLEvent event = reader.cur();
            if (event.isStartElement()) {
                QName eventElemName = event.asStartElement().getName();
                if (eventElemName.equals(name)) {
                    reader.next();
                    skipSpaces.run(reader);
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
        return (reader, _pool) -> {
            XMLEvent event = reader.cur();
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
        return (reader, _pool) -> {
            XMLEvent event = reader.cur();
            if (event.isEndElement()) {
                QName eventElemName = event.asEndElement().getName();
                if (eventElemName.equals(name)) {
                    reader.next();
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
        return (reader, _pool) -> {
            for (;;) {
                XMLEvent event = reader.cur();
                if (event.isEndElement()) {
                    QName eventElemName = event.asEndElement().getName();
                    if (eventElemName.equals(name)) {
                        reader.next();
                        skipSpaces.run(reader);
                        return true;
                    }
                } else if (!event.isCharacters() || !event.asCharacters().isWhiteSpace()) {
                    return false;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                        "`tryClose(" + name + ")` called at the end of stream."
                    );
                }
                reader.next();
            }
        };
    }

    public static Parser1<Boolean> tryClose(String name) {
        return tryClose(new QName(name));
    }

    public static Parser1<Boolean> closing(QName name) {
        return (reader, _pool) -> {
            XMLEvent event = reader.cur();
            if (event.isEndElement()) {
                QName eventElemName = event.asEndElement().getName();
                if (eventElemName.equals(name)) return true;
            }
            return false;
        };
    }

    public static Parser1<Boolean> closing(String name) {
        return closing(new QName(name));
    }

    public static Parser1<String> text = (reader, _pool) -> {
        XMLEvent event = reader.cur();
        if (!event.isCharacters()) {
            if (event.isEndElement()) return "";
            throw new ParserException("`text` called on non-characters data.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (event.isCharacters()) {
            stringBuilder.append(event.asCharacters().getData());
            if (reader.hasNext()) {
                event = reader.next();
            }
        }
        return stringBuilder.toString();
    };

    public static Parser1<String> attr(QName name) {
        return (reader, _pool) -> {
            XMLEvent event = reader.cur();
            if (!event.isStartElement()) {
                throw new ParserException(
                    "`attr(" + name + ")` called at non-opening element."
                );
            }
            return reader.attr(name).getValue();
        };
    }

    public static Parser1<String> attr(String name) {
        return attr(new QName(name));
    }

    public static Parser1<Map<QName, String>> attrs = (reader, _pool) -> {
        if (!reader.cur().isStartElement()) {
            throw new ParserException("`attrs` called at non-opening element.");
        }
        Map<QName, String> attrs = new HashMap<>();
        Iterator<Attribute> iterator = reader.attrs();
        while (iterator.hasNext()) {
            Attribute attr = iterator.next();
            attrs.put(attr.getName(), attr.getValue());
        }
        return attrs;
    };

    //-------------------------------------------------------------------------------------------//

    public static <A,B> Parser2<A,B> seq(Parser<A> pA, Parser<B> pB) {
        return (r, p) -> {
            if (p == null) {
                return new Tuple2<>(
                    pA.run(r),
                    pB.run(r)
                );
            }
            p.$1 = pA.run(r, p.$1);
            p.$2 = pB.run(r, p.$2);
            return p;
        };
    }

    public static <A,B,C> Parser3<A,B,C> seq(Parser<A> pA, Parser<B> pB, Parser<C> pC) {
        return (r, p) -> {
            if (p == null) {
                return new Tuple3<>(
                    pA.run(r),
                    pB.run(r),
                    pC.run(r)
                );
            }
            p.$1 = pA.run(r, p.$1);
            p.$2 = pB.run(r, p.$2);
            p.$3 = pC.run(r, p.$3);
            return p;
        };
    }

    public static <A,B,C,D> Parser4<A,B,C,D> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD
    ) {
        return (r, p) -> {
            if (p == null) {
                return new Tuple4<>(
                    pA.run(r),
                    pB.run(r),
                    pC.run(r),
                    pD.run(r)
                );
            }
            p.$1 = pA.run(r, p.$1);
            p.$2 = pB.run(r, p.$2);
            p.$3 = pC.run(r, p.$3);
            p.$4 = pD.run(r, p.$4);
            return p;
        };
    }

    public static <A,B,C,D,E> Parser5<A,B,C,D,E> seq(
            Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
            Parser<E> pE
    ) {
        return (r, p) -> {
            if (p == null) {
                return new Tuple5<>(
                    pA.run(r),
                    pB.run(r),
                    pC.run(r),
                    pD.run(r),
                    pE.run(r)
                );
            }
            p.$1 = pA.run(r, p.$1);
            p.$2 = pB.run(r, p.$2);
            p.$3 = pC.run(r, p.$3);
            p.$4 = pD.run(r, p.$4);
            p.$5 = pE.run(r, p.$5);
            return p;
        };
    }

    public static <A,B,C,D,E,F> Parser6<A,B,C,D,E,F> seq(
            Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
            Parser<E> pE, Parser<F> pF
    ) {
        return (r, p) -> {
            if (p == null) {
                return new Tuple6<>(
                    pA.run(r),
                    pB.run(r),
                    pC.run(r),
                    pD.run(r),
                    pE.run(r),
                    pF.run(r)
                );
            }
            p.$1 = pA.run(r, p.$1);
            p.$2 = pB.run(r, p.$2);
            p.$3 = pC.run(r, p.$3);
            p.$4 = pD.run(r, p.$4);
            p.$5 = pE.run(r, p.$5);
            p.$6 = pF.run(r, p.$6);
            return p;
        };
    }

    public static <A,B,C,D,E,F,G> Parser7<A,B,C,D,E,F,G> seq(
            Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
            Parser<E> pE, Parser<F> pF, Parser<G> pG
    ) {
        return (r, p) -> {
            if (p == null) {
                return new Tuple7<>(
                    pA.run(r),
                    pB.run(r),
                    pC.run(r),
                    pD.run(r),
                    pE.run(r),
                    pF.run(r),
                    pG.run(r)
                );
            }
            p.$1 = pA.run(r, p.$1);
            p.$2 = pB.run(r, p.$2);
            p.$3 = pC.run(r, p.$3);
            p.$4 = pD.run(r, p.$4);
            p.$5 = pE.run(r, p.$5);
            p.$6 = pF.run(r, p.$6);
            p.$7 = pG.run(r, p.$7);
            return p;
        };
    }

    public static <A,B,C,D,E,F,G,H> Parser8<A,B,C,D,E,F,G,H> seq(
            Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
            Parser<E> pE, Parser<F> pF, Parser<G> pG, Parser<H> pH
    ) {
        return (r, p) -> {
            if (p == null) {
                return new Tuple8<>(
                    pA.run(r),
                    pB.run(r),
                    pC.run(r),
                    pD.run(r),
                    pE.run(r),
                    pF.run(r),
                    pG.run(r),
                    pH.run(r)
                );
            }
            p.$1 = pA.run(r, p.$1);
            p.$2 = pB.run(r, p.$2);
            p.$3 = pC.run(r, p.$3);
            p.$4 = pD.run(r, p.$4);
            p.$5 = pE.run(r, p.$5);
            p.$6 = pF.run(r, p.$6);
            p.$7 = pG.run(r, p.$7);
            p.$8 = pH.run(r, p.$8);
            return p;
        };
    }

    //-------------------------------------------------------------------------------------------//

    public static <X> Parser1<X> open(QName name, Parser<X> p) {
        return opens(name).nextR(p).nextL(step);
    }

    public static <X> Parser1<X> open(String name, Parser<X> p) {
        return open(new QName(name), p);
    }

    public static <X> Parser1<X> elem(QName name, Parser<X> p) {
        return opens(name).nextR(p).nextL(step).nextL(close(name));
    }

    public static <X> Parser1<X> elem(String name, Parser<X> p) {
        return elem(new QName(name), p);
    }

    public static Parser1<String> elemAttr(QName elemName, QName attrName) {
        return opens(elemName).nextR(attr(attrName)).nextL(step).nextL(close(elemName));
    }

    public static Parser1<String> elemAttr(String elemName, String attrName) {
        return elemAttr(new QName(elemName), new QName(attrName));
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
        return within(elemName, attr(attrName), text);
    }

    public static Parser2<String,String> elemAttrAndText(String elemName, String attrName) {
        return elemAttrAndText(new QName(elemName), new QName(attrName));
    }

    public static Parser2<Map<QName,String>,String> elemAttrsAndText(QName name) {
        return within(name, attrs, text);
    }

    public static Parser2<Map<QName,String>,String> elemAttrsAndText(String name) {
        return elemAttrsAndText(new QName(name));
    }

    //-------------------------------------------------------------------------------------------//

    public static <X> Parser1<X> within(QName name, Parser<X> p) {
        return open(name, p).nextL(close(name))::run;
    }

    public static <X> Parser1<X> within(String name, Parser<X> p) {
        return within(new QName(name), p);
    }

    public static <T,A> Parser2<T,A> within(QName name, Parser<T> t, Parser<A> pA) {
        return seq(open(name, t), pA).nextL(close(name));
    }

    public static <T,A,B> Parser3<T,A,B> within(QName name,
        Parser<T> t, Parser<A> pA, Parser<B> pB
    ) {
        return seq(open(name, t), pA, pB).nextL(close(name));
    }

    public static <T,A,B,C> Parser4<T,A,B,C> within(QName name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC
    ) {
        return seq(open(name, t), pA, pB, pC).nextL(close(name));
    }

    public static <T,A,B,C,D> Parser5<T,A,B,C,D> within(QName name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD
    ) {
        return seq(open(name, t), pA, pB, pC, pD).nextL(close(name));
    }

    public static <T,A,B,C,D,E> Parser6<T,A,B,C,D,E> within(QName name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE
    ) {
        return seq(open(name, t), pA, pB, pC, pD, pE).nextL(close(name));
    }

    public static <T,A,B,C,D,E,F> Parser7<T,A,B,C,D,E,F> within(QName name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE, Parser<F> pF
    ) {
        return seq(open(name, t), pA, pB, pC, pD, pE, pF).nextL(close(name));
    }

    public static <T,A,B,C,D,E,F,G> Parser8<T,A,B,C,D,E,F,G> within(QName name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE, Parser<F> pF, Parser<G> pG
    ) {
        return seq(open(name, t), pA, pB, pC, pD, pE, pF, pG).nextL(close(name));
    }

    public static <T,A> Parser2<T,A> within(String name,
        Parser<T> t, Parser<A> pA
    ) {
        return within(new QName(name), t, pA);
    }

    public static <T,A,B> Parser3<T,A,B> within(String name,
        Parser<T> t, Parser<A> pA, Parser<B> pB
    ) {
        return within(new QName(name), t, pA, pB);
    }

    public static <T,A,B,C> Parser4<T,A,B,C> within(String name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC
    ) {
        return within(new QName(name), t, pA, pB, pC);
    }

    public static <T,A,B,C,D> Parser5<T,A,B,C,D> within(String name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD
    ) {
        return within(new QName(name), t, pA, pB, pC, pD);
    }

    public static <T,A,B,C,D,E> Parser6<T,A,B,C,D,E> within(String name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE
    ) {
        return within(new QName(name), t, pA, pB, pC, pD, pE);
    }

    public static <T,A,B,C,D,E,F> Parser7<T,A,B,C,D,E,F> within(String name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE, Parser<F> pF
    ) {
        return within(new QName(name), t, pA, pB, pC, pD, pE, pF);
    }

    public static <T,A,B,C,D,E,F,G> Parser8<T,A,B,C,D,E,F,G> within(String name,
        Parser<T> t, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE, Parser<F> pF, Parser<G> pG
    ) {
        return within(new QName(name), t, pA, pB, pC, pD, pE, pF, pG);
    }

    //-------------------------------------------------------------------------------------------//

    public static <X> Parser1<List<X>> manyWithin(QName name, Parser<X> p) {
        return open(name).nextR(p.until(tryClose(name)));
    }

    public static <X> Parser1<List<X>> manyWithin(String name, Parser<X> p) {
        return manyWithin(new QName(name), p);
    }

    public static <X,Y> Parser2<X,List<Y>> manyWithin(QName name, Parser<X> t, Parser<Y> p) {
        return seq(opens(name).nextR(t).nextL(step), p.until(tryClose(name)));
    }

    public static <X,Y> Parser2<X,List<Y>> manyWithin(String name, Parser<X> t, Parser<Y> p) {
        return manyWithin(new QName(name), t, p);
    }

    public static <X> Parser<Stream<X>> streamManyWithin(QName name, Parser<X> p) {
        return open(name).nextR(p.streamUntil(tryClose(name)));
    }

    public static <X> Parser<Stream<X>> streamManyWithin(String name, Parser<X> p) {
        return streamManyWithin(new QName(name), p);
    }

    public static <X,Y> Parser<Tuple2<X,Stream<Y>>> streamManyWithin(QName name, Parser<X> t, Parser<Y> p) {
        return seq(opens(name).nextR(t).nextL(step), p.streamUntil(tryClose(name)));
    }

    public static <X,Y> Parser<Tuple2<X,Stream<Y>>> streamManyWithin(String name, Parser<X> t, Parser<Y> p) {
        return streamManyWithin(new QName(name), t, p);
    }

    public static <B> Parser<?> evalManyWithin(QName name, Parser<?> bodyParser) {
        return open(name).nextL(bodyParser.until_(tryClose(name)));
    }

    public static Parser<?> evalManyWithin(String name, Parser<?> p) {
        return evalManyWithin(new QName(name), p);
    }

    public static <T> Parser<T> evalManyWithin(
        QName name, Parser<T> targetParser, Parser<?> bodyParser
    ) {
        return opens(name).nextR(targetParser).nextL(step).nextL(bodyParser.until_(tryClose(name)));
    }

    public static <X> Parser<X> evalManyWithin(String name, Parser<X> t, Parser<?> p) {
        return evalManyWithin(new QName(name), t, p);
    }
}
