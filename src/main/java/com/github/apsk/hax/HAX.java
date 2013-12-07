package com.github.apsk.hax;

import com.github.apsk.j8t.*;

import javax.xml.namespace.QName;
import java.util.List;

public final class HAX {
    public static Parser<?> skipSpaces = (reader, _pool) -> {
        while (reader.isCharacters() && reader.isWhiteSpace()) {
            reader.next();
        }
        return null;
    };

    public static Parser<?> step = (reader, _pool) -> {
        reader.next();
        skipSpaces.run(reader);
        return null;
    };

    public static Parser<?> skipTo(QName name) {
        return (reader, _pool) -> {
            for (;;) {
                if (reader.isStartElement()) {
                    QName eventElemName = reader.getName();
                    if (eventElemName.equals(name))
                        return null;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                        "`skipTo(" + name + ")` reached the end of stream.");
                }
                reader.next();
            }
        };
    }

    public static Parser<?> skipTo(String name) {
        return (reader, _pool) -> {
            for (;;) {
                if (reader.isStartElement()) {
                    if (reader.getLocalName().equals(name))
                        return null;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                        "`skipTo(" + name + ")` reached the end of stream.");
                }
                reader.next();
            }
        };
    }

    public static Parser<?> open(QName name) {
        return (reader, _pool) -> {
            if (reader.isStartElement()) {
                QName eventElemName = reader.getName();
                if (eventElemName.equals(name)) {
                    reader.next();
                    skipSpaces.run(reader);
                    return null;
                } else {
                    throw new ParserException(
                        "`open(" + name + ")` called on wrong opening element.");
                }
            } else {
                throw new ParserException(
                    "`open(" + name + ")` called on non-opening element.");
            }
        };
    }

    public static Parser<?> open(String name) {
        return (reader, _pool) -> {
            if (reader.isStartElement()) {
                if (reader.getLocalName().equals(name)) {
                    reader.next();
                    skipSpaces.run(reader);
                    return null;
                } else {
                    throw new ParserException(
                        "`open(" + name + ")` called on wrong opening element.");
                }
            } else {
                throw new ParserException(
                    "`open(" + name + ")` called on non-opening element.");
            }
        };
    }

    public static Parser<?> opens(QName name) {
        return (reader, _pool) -> {
            if (reader.isStartElement()) {
                if (reader.getName().equals(name))
                    return null;
                else {
                    throw new ParserException(
                        "`opens(" + name + ")` called on wrong opening element.");
                }
            } else {
                throw new ParserException(
                    "`opens(" + name + ")` called on non-opening element.");
            }
        };
    }

    public static Parser<?> opens(String name) {
        return (reader, _pool) -> {
            if (reader.isStartElement()) {
                if (reader.getLocalName().equals(name))
                    return null;
                else {
                    throw new ParserException(
                        "`opens(" + name + ")` called on wrong opening element.");
                }
            } else {
                throw new ParserException(
                    "`opens(" + name + ")` called on non-opening element.");
            }
        };
    }

    public static Parser<?> close(QName name) {
        return (reader, _pool) -> {
            if (reader.isEndElement()) {
                if (reader.getName().equals(name)) {
                    reader.next();
                    return null;
                } else {
                    throw new ParserException(
                        "`close(" + name + ")` called on wrong closing element.");
                }
            } else {
                throw new ParserException(
                    "`close(" + name + ")` called on non-closing element.");
            }
        };
    }

    public static Parser<?> close(String name) {
        return (reader, _pool) -> {
            if (reader.isEndElement()) {
                if (reader.getLocalName().equals(name)) {
                    reader.next();
                    return null;
                } else {
                    throw new ParserException(
                        "`close(" + name + ")` called on wrong closing element.");
                }
            } else {
                throw new ParserException(
                    "`close(" + name + ")` called on non-closing element.");
            }
        };
    }

    public static Parser<Boolean> tryClose(QName name) {
        return (reader, _pool) -> {
            for (;;) {
                if (reader.isEndElement()) {
                    if (reader.getName().equals(name)) {
                        reader.next();
                        skipSpaces.run(reader);
                        return true;
                    }
                } else if (!reader.isCharacters() || !reader.isWhiteSpace()) {
                    return false;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                        "`tryClose(" + name + ")` called at the end of stream.");
                }
                reader.next();
            }
        };
    }

    public static Parser<Boolean> tryClose(String name) {
        return (reader, _pool) -> {
            for (;;) {
                if (reader.isEndElement()) {
                    if (reader.getLocalName().equals(name)) {
                        reader.next();
                        skipSpaces.run(reader);
                        return true;
                    }
                } else if (!reader.isCharacters() || !reader.isWhiteSpace()) {
                    return false;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                            "`tryClose(" + name + ")` called at the end of stream.");
                }
                reader.next();
            }
        };
    }

    public static Parser<Boolean> closing(QName name) {
        return (reader, _pool) -> {
            if (reader.isEndElement()) {
                if (reader.getName().equals(name))
                    return true;
            }
            return false;
        };
    }

    public static Parser<Boolean> closing(String name) {
        return (reader, _pool) -> {
            if (reader.isEndElement()) {
                if (reader.getLocalName().equals(name))
                    return true;
            }
            return false;
        };
    }

    public static Parser<String> text = (reader, _pool) -> {
        if (!reader.isCharacters()) {
            if (reader.isEndElement())
                return "";
            throw new ParserException("`text` called on non-characters data.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (reader.isCharacters()) {
            stringBuilder.append(
                reader.getTextCharacters(),
                reader.getTextStart(),
                reader.getTextLength()
            );
            if (reader.hasNext())
                reader.next();
        }
        return stringBuilder.toString();
    };

    public static Parser<String> attr(QName name) {
        return (reader, _pool) -> {
            if (!reader.isStartElement()) {
                throw new ParserException(
                    "`attr(" + name + ")` called at non-opening element.");
            }
            return reader.getAttributeValue(name.getNamespaceURI(), name.getLocalPart());
        };
    }

    public static Parser<String> attr(String name) {
        return (reader, _pool) -> {
            if (!reader.isStartElement()) {
                throw new ParserException(
                    "`attr(" + name + ")` called at non-opening element.");
            }
            return reader.getAttributeValue(null, name);
        };
    }

    //-------------------------------------------------------------------------------------------//

    public static <A,B> Parser<Tuple2<A,B>> rawSeq(Parser<A> pA, Parser<B> pB) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple2<>(
                    pA.run(reader),
                    pB.run(reader)
                );
            }
            pool.$1 = pA.run(reader, pool.$1);
            pool.$2 = pB.run(reader, pool.$2);
            return pool;
        };
    }

    public static <A,B,C> Parser<Tuple3<A,B,C>> rawSeq(Parser<A> pA, Parser<B> pB, Parser<C> pC) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple3<>(
                    pA.run(reader),
                    pB.run(reader),
                    pC.run(reader)
                );
            }
            pool.$1 = pA.run(reader, pool.$1);
            pool.$2 = pB.run(reader, pool.$2);
            pool.$3 = pC.run(reader, pool.$3);
            return pool;
        };
    }

    public static <A,B,C,D> Parser<Tuple4<A,B,C,D>> rawSeq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple4<>(
                    pA.run(reader),
                    pB.run(reader),
                    pC.run(reader),
                    pD.run(reader)
                );
            }
            pool.$1 = pA.run(reader, pool.$1);
            pool.$2 = pB.run(reader, pool.$2);
            pool.$3 = pC.run(reader, pool.$3);
            pool.$4 = pD.run(reader, pool.$4);
            return pool;
        };
    }

    public static <A,B,C,D,E> Parser<Tuple5<A,B,C,D,E>> rawSeq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple5<>(
                    pA.run(reader),
                    pB.run(reader),
                    pC.run(reader),
                    pD.run(reader),
                    pE.run(reader)
                );
            }
            pool.$1 = pA.run(reader, pool.$1);
            pool.$2 = pB.run(reader, pool.$2);
            pool.$3 = pC.run(reader, pool.$3);
            pool.$4 = pD.run(reader, pool.$4);
            pool.$5 = pE.run(reader, pool.$5);
            return pool;
        };
    }

    public static <A,B,C,D,E,F> Parser<Tuple6<A,B,C,D,E,F>> rawSeq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE, Parser<F> pF
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple6<>(
                    pA.run(reader),
                    pB.run(reader),
                    pC.run(reader),
                    pD.run(reader),
                    pE.run(reader),
                    pF.run(reader)
                );
            }
            pool.$1 = pA.run(reader, pool.$1);
            pool.$2 = pB.run(reader, pool.$2);
            pool.$3 = pC.run(reader, pool.$3);
            pool.$4 = pD.run(reader, pool.$4);
            pool.$5 = pE.run(reader, pool.$5);
            pool.$6 = pF.run(reader, pool.$6);
            return pool;
        };
    }

    public static <A,B,C,D,E,F,G> Parser<Tuple7<A,B,C,D,E,F,G>> rawSeq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE, Parser<F> pF, Parser<G> pG
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple7<>(
                    pA.run(reader),
                    pB.run(reader),
                    pC.run(reader),
                    pD.run(reader),
                    pE.run(reader),
                    pF.run(reader),
                    pG.run(reader)
                );
            }
            pool.$1 = pA.run(reader, pool.$1);
            pool.$2 = pB.run(reader, pool.$2);
            pool.$3 = pC.run(reader, pool.$3);
            pool.$4 = pD.run(reader, pool.$4);
            pool.$5 = pE.run(reader, pool.$5);
            pool.$6 = pF.run(reader, pool.$6);
            pool.$7 = pG.run(reader, pool.$7);
            return pool;
        };
    }

    public static <A,B,C,D,E,F,G,H> Parser<Tuple8<A,B,C,D,E,F,G,H>> rawSeq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE, Parser<F> pF, Parser<G> pG, Parser<H> pH
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple8<>(
                    pA.run(reader),
                    pB.run(reader),
                    pC.run(reader),
                    pD.run(reader),
                    pE.run(reader),
                    pF.run(reader),
                    pG.run(reader),
                    pH.run(reader)
                );
            }
            pool.$1 = pA.run(reader, pool.$1);
            pool.$2 = pB.run(reader, pool.$2);
            pool.$3 = pC.run(reader, pool.$3);
            pool.$4 = pD.run(reader, pool.$4);
            pool.$5 = pE.run(reader, pool.$5);
            pool.$6 = pF.run(reader, pool.$6);
            pool.$7 = pG.run(reader, pool.$7);
            pool.$8 = pH.run(reader, pool.$8);
            return pool;
        };
    }

    public static <A,B> PooledParser<Tuple2<A,B>> seq(Parser<A> pA, Parser<B> pB) {
        return PooledParser.from(rawSeq(
            pA.purify(), pB.purify()
        ));
    }

    public static <A,B,C> PooledParser<Tuple3<A,B,C>> seq(Parser<A> pA, Parser<B> pB, Parser<C> pC) {
        return PooledParser.from(rawSeq(
            pA.purify(), pB.purify(), pC.purify()
        ));
    }

    public static <A,B,C,D> PooledParser<Tuple4<A,B,C,D>> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD
    ) {
        return PooledParser.from(rawSeq(
            pA.purify(), pB.purify(), pC.purify(), pD.purify()
        ));
    }

    public static <A,B,C,D,E> PooledParser<Tuple5<A,B,C,D,E>> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE
    ) {
        return PooledParser.from(rawSeq(
            pA.purify(), pB.purify(), pC.purify(), pD.purify(), pE.purify()
        ));
    }

    public static <A,B,C,D,E,F> PooledParser<Tuple6<A,B,C,D,E,F>> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE, Parser<F> pF
    ) {
        return PooledParser.from(rawSeq(
            pA.purify(), pB.purify(), pC.purify(),
            pD.purify(), pE.purify(), pF.purify()
        ));
    }

    public static <A,B,C,D,E,F,G> PooledParser<Tuple7<A,B,C,D,E,F,G>> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE, Parser<F> pF, Parser<G> pG
    ) {
        return PooledParser.from(rawSeq(
            pA.purify(), pB.purify(), pC.purify(), pD.purify(),
            pE.purify(), pF.purify(), pG.purify()
        ));
    }

    public static <A,B,C,D,E,F,G,H> PooledParser<Tuple8<A,B,C,D,E,F,G,H>> seq(
        Parser<A> pA, Parser<B> pB, Parser<C> pC, Parser<D> pD,
        Parser<E> pE, Parser<F> pF, Parser<G> pG, Parser<H> pH
    ) {
        return PooledParser.from(rawSeq(
            pA.purify(), pB.purify(), pC.purify(), pD.purify(),
            pE.purify(), pF.purify(), pG.purify(), pH.purify()
        ));
    }

    //-------------------------------------------------------------------------------------------//

    public static <X> Parser<X> open(QName name, Parser<X> p) {
        return opens(name).nextR(p).nextL(step);
    }

    public static <X> Parser<X> open(String name, Parser<X> p) {
        return opens(name).nextR(p).nextL(step);
    }

    public static <X> Parser<X> elem(QName name, Parser<X> p) {
        return opens(name).nextR(p).nextL(step).nextL(close(name));
    }

    public static <X> Parser<X> elem(String name, Parser<X> p) {
        return opens(name).nextR(p).nextL(step).nextL(close(name));
    }

    public static Parser<String> elemAttr(QName elemName, QName attrName) {
        return opens(elemName).nextR(attr(attrName)).nextL(step).nextL(close(elemName));
    }

    public static Parser<String> elemAttr(String elemName, String attrName) {
        return opens(elemName).nextR(attr(attrName)).nextL(step).nextL(close(elemName));
    }

    public static Parser<String> elemText(QName name) {
        return open(name).nextR(text).nextL(close(name));
    }

    public static Parser<String> elemText(String name) {
        return open(name).nextR(text).nextL(close(name));
    }

    public static Parser<Tuple2<String,String>> elemAttrAndText(QName elemName, QName attrName) {
        return within(elemName, attr(attrName), text);
    }

    public static Parser<Tuple2<String,String>> elemAttrAndText(String elemName, String attrName) {
        return within(elemName, attr(attrName), text);
    }

    //-------------------------------------------------------------------------------------------//

    public static <A> Parser<A> within(QName name, Parser<A> bodyParser) {
        return open(name).nextR(bodyParser).nextL(close(name));
    }

    public static <A> Parser<A> within(String name, Parser<A> bodyParser) {
        return open(name).nextR(bodyParser).nextL(close(name));
    }

    public static <T,A> Parser<Tuple2<T,A>> within(QName name, Parser<T> pT, Parser<A> pA) {
        return seq(open(name, pT), pA).nextL(close(name));
    }

    public static <T,A,B> Parser<Tuple3<T,A,B>> within(QName name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB
    ) {
        return seq(open(name, pT), pA, pB).nextL(close(name));
    }

    public static <T,A,B,C> Parser<Tuple4<T,A,B,C>> within(QName name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC
    ) {
        return seq(open(name, pT), pA, pB, pC).nextL(close(name));
    }

    public static <T,A,B,C,D> Parser<Tuple5<T,A,B,C,D>> within(QName name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD
    ) {
        return seq(open(name, pT), pA, pB, pC, pD).nextL(close(name));
    }

    public static <T,A,B,C,D,E> Parser<Tuple6<T,A,B,C,D,E>> within(QName name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE
    ) {
        return seq(open(name, pT), pA, pB, pC, pD, pE).nextL(close(name));
    }

    public static <T,A,B,C,D,E,F> Parser<Tuple7<T,A,B,C,D,E,F>> within(QName name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE, Parser<F> pF
    ) {
        return seq(open(name, pT), pA, pB, pC, pD, pE, pF).nextL(close(name));
    }

    public static <T,A,B,C,D,E,F,G> Parser<Tuple8<T,A,B,C,D,E,F,G>> within(QName name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE, Parser<F> pF, Parser<G> pG
    ) {
        return seq(open(name, pT), pA, pB, pC, pD, pE, pF, pG).nextL(close(name));
    }

    //-------------------------------------------------------------------------------------------//

    public static <T,A> Parser<Tuple2<T,A>> within(String name,
       Parser<T> pT, Parser<A> pA
    ) {
        return seq(open(name, pT), pA).nextL(close(name));
    }

    public static <T,A,B> Parser<Tuple3<T,A,B>> within(String name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB
    ) {
        return seq(open(name, pT), pA, pB).nextL(close(name));
    }

    public static <T,A,B,C> Parser<Tuple4<T,A,B,C>> within(String name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC
    ) {
        return seq(open(name, pT), pA, pB, pC).nextL(close(name));
    }

    public static <T,A,B,C,D> Parser<Tuple5<T,A,B,C,D>> within(String name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD
    ) {
        return seq(open(name, pT), pA, pB, pC, pD).nextL(close(name));
    }

    public static <T,A,B,C,D,E> Parser<Tuple6<T,A,B,C,D,E>> within(String name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE
    ) {
        return seq(open(name, pT), pA, pB, pC, pD, pE).nextL(close(name));
    }

    public static <T,A,B,C,D,E,F> Parser<Tuple7<T,A,B,C,D,E,F>> within(String name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE, Parser<F> pF
    ) {
        return seq(open(name, pT), pA, pB, pC, pD, pE, pF).nextL(close(name));
    }

    public static <T,A,B,C,D,E,F,G> Parser<Tuple8<T,A,B,C,D,E,F,G>> within(String name,
        Parser<T> pT, Parser<A> pA, Parser<B> pB, Parser<C> pC,
        Parser<D> pD, Parser<E> pE, Parser<F> pF, Parser<G> pG
    ) {
        return seq(open(name, pT), pA, pB, pC, pD, pE, pF, pG).nextL(close(name));
    }

    //-------------------------------------------------------------------------------------------//

    public static <X> Parser<List<X>> manyWithin(QName name, Parser<X> bodyParser) {
        return open(name).nextR(bodyParser.until(tryClose(name)));
    }

    public static <X> Parser<List<X>> manyWithin(String name, Parser<X> bodyParser) {
        return open(name).nextR(bodyParser.until(tryClose(name)));
    }

    public static <T,B> Parser<Tuple2<T,List<B>>> manyWithin(
        QName name, Parser<T> targetParser, Parser<B> bodyParser
    ) {
        return seq(opens(name).nextR(targetParser).nextL(step), bodyParser.until(tryClose(name)));
    }

    public static <T,B> Parser<Tuple2<T,List<B>>> manyWithin(
        String name, Parser<T> targetParser, Parser<B> bodyParser
    ) {
        return seq(opens(name).nextR(targetParser).nextL(step), bodyParser.until(tryClose(name)));
    }

    public static Parser<?> evalManyWithin(QName name, Parser<?> bodyParser) {
        return open(name).nextL(bodyParser.until_(tryClose(name)));
    }

    public static Parser<?> evalManyWithin(String name, Parser<?> bodyParser) {
        return open(name).nextL(bodyParser.until_(tryClose(name)));
    }

    public static <T> Parser<T> evalManyWithin(
        QName name, Parser<T> targetParser, Parser<?> bodyParser
    ) {
        return opens(name).nextR(targetParser).nextL(step).nextL(bodyParser.until_(tryClose(name)));
    }

    public static <T> Parser<T> evalManyWithin(
        String name, Parser<T> targetParser, Parser<?> bodyParser
    ) {
        return opens(name).nextR(targetParser).nextL(step).nextL(bodyParser.until_(tryClose(name)));
    }
}
