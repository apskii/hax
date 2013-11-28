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

    public static Parser1<Boolean> closing(QName name) {
        return r -> {
            XMLEvent event = r.cur();
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

    public static <A,B> Parser2<A,B> seq(Parser<A> pA, Parser<B> pB) {
        return r -> new Tuple2<>(pA.run(r), pB.run(r));
    }

    public static <A,B,C> Parser3<A,B,C> seq(Parser<A> pA, Parser<B> pB, Parser<C> pC) {
        return r -> new Tuple3<>(pA.run(r), pB.run(r), pC.run(r));
    }

    public static <A,B,C,D> Parser4<A,B,C,D> seq(Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD) {
        return r -> new Tuple4<>(pA.run(r), pB.run(r), pC.run(r), pD.run(r));
    }

    public static <A,B,C,D,E> Parser5<A,B,C,D,E> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE
    ) {
        return r -> new Tuple5<>(pA.run(r), pB.run(r), pC.run(r), pD.run(r), pE.run(r));
    }

    public static <A,B,C,D,E,F> Parser6<A,B,C,D,E,F> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE, Parser<F> pF
    ) {
        return r -> new Tuple6<>(pA.run(r), pB.run(r), pC.run(r), pD.run(r), pE.run(r), pF.run(r));
    }

    public static <A,B,C,D,E,F,G> Parser7<A,B,C,D,E,F,G> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE, Parser<F> pF, Parser<G> pG
    ) {
        return r -> new Tuple7<>(
            pA.run(r), pB.run(r), pC.run(r), pD.run(r),
            pE.run(r), pF.run(r), pG.run(r)
        );
    }

    public static <A,B,C,D,E,F,G,H> Parser8<A,B,C,D,E,F,G,H> seq(
            Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
            Parser<E> pE, Parser<F> pF, Parser<G> pG, Parser<H> pH
    ) {
        return r -> new Tuple8<>(
            pA.run(r), pB.run(r), pC.run(r), pD.run(r),
            pE.run(r), pF.run(r), pG.run(r), pH.run(r)
        );
    }

    /////////////////////////////////////////////////////////////////////////////////////

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
        return elem(name, p);
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

    public static <X> Parser1<List<X>> manyWithin(QName name, Parser<X> p) {
        return open(name).nextR(p.until(tryClose(name)));
    }

    public static <X> Parser1<List<X>> manyWithin(String name, Parser<X> p) {
        return manyWithin(new QName(name), p);
    }

    public static <X,Y> Parser2<X,List<Y>> manyWithin(QName name, Parser<X> t, Parser<Y> p) {
        return opens(name).nextR(t).nextL(step).and(p.until(tryClose(name)));
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
        return opens(name).nextR(t).nextL(step).and(p.streamUntil(tryClose(name)));
    }

    public static <X,Y> Parser<Tuple2<X,Stream<Y>>> streamManyWithin(String name, Parser<X> t, Parser<Y> p) {
        return streamManyWithin(new QName(name), t, p);
    }

    public static Parser<?> evalManyWithin(QName name, Parser<?> p) {
        return r -> {
            open(name).run(r);
            while (!tryClose(name).run(r)) p.run(r);
            return null;
        };
    }

    public static Parser<?> evalManyWithin(String name, Parser<?> p) {
        return evalManyWithin(new QName(name), p);
    }

    public static <X> Parser<X> evalManyWithin(QName name, Parser<X> t, Parser<?> p) {
        return r -> {
            X x = opens(name).nextR(t).nextL(step).run(r);
            while (!tryClose(name).run(r)) p.run(r);
            return x;
        };
    }

    public static <X> Parser<X> evalManyWithin(String name, Parser<X> t, Parser<?> p) {
        return evalManyWithin(new QName(name), t, p);
    }
}
