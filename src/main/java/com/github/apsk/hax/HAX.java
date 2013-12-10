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

    public static <A,B> Parser<Tuple2<A,B>> rawSeq(Parser<A> bodyParserA, Parser<B> bodyParserB) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple2<>(
                    bodyParserA.run(reader),
                    bodyParserB.run(reader)
                );
            }
            pool.$1 = bodyParserA.run(reader, pool.$1);
            pool.$2 = bodyParserB.run(reader, pool.$2);
            return pool;
        };
    }

    public static <A,B,C> Parser<Tuple3<A,B,C>> rawSeq(Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple3<>(
                    bodyParserA.run(reader),
                    bodyParserB.run(reader),
                    bodyParserC.run(reader)
                );
            }
            pool.$1 = bodyParserA.run(reader, pool.$1);
            pool.$2 = bodyParserB.run(reader, pool.$2);
            pool.$3 = bodyParserC.run(reader, pool.$3);
            return pool;
        };
    }

    public static <A,B,C,D> Parser<Tuple4<A,B,C,D>> rawSeq(
        Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple4<>(
                    bodyParserA.run(reader),
                    bodyParserB.run(reader),
                    bodyParserC.run(reader),
                    bodyParserD.run(reader)
                );
            }
            pool.$1 = bodyParserA.run(reader, pool.$1);
            pool.$2 = bodyParserB.run(reader, pool.$2);
            pool.$3 = bodyParserC.run(reader, pool.$3);
            pool.$4 = bodyParserD.run(reader, pool.$4);
            return pool;
        };
    }

    public static <A,B,C,D,E> Parser<Tuple5<A,B,C,D,E>> rawSeq(
        Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
        Parser<E> bodyParserE
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple5<>(
                    bodyParserA.run(reader),
                    bodyParserB.run(reader),
                    bodyParserC.run(reader),
                    bodyParserD.run(reader),
                    bodyParserE.run(reader)
                );
            }
            pool.$1 = bodyParserA.run(reader, pool.$1);
            pool.$2 = bodyParserB.run(reader, pool.$2);
            pool.$3 = bodyParserC.run(reader, pool.$3);
            pool.$4 = bodyParserD.run(reader, pool.$4);
            pool.$5 = bodyParserE.run(reader, pool.$5);
            return pool;
        };
    }

    public static <A,B,C,D,E,F> Parser<Tuple6<A,B,C,D,E,F>> rawSeq(
        Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
        Parser<E> bodyParserE, Parser<F> bodyParserF
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple6<>(
                    bodyParserA.run(reader),
                    bodyParserB.run(reader),
                    bodyParserC.run(reader),
                    bodyParserD.run(reader),
                    bodyParserE.run(reader),
                    bodyParserF.run(reader)
                );
            }
            pool.$1 = bodyParserA.run(reader, pool.$1);
            pool.$2 = bodyParserB.run(reader, pool.$2);
            pool.$3 = bodyParserC.run(reader, pool.$3);
            pool.$4 = bodyParserD.run(reader, pool.$4);
            pool.$5 = bodyParserE.run(reader, pool.$5);
            pool.$6 = bodyParserF.run(reader, pool.$6);
            return pool;
        };
    }

    public static <A,B,C,D,E,F,G> Parser<Tuple7<A,B,C,D,E,F,G>> rawSeq(
        Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
        Parser<E> bodyParserE, Parser<F> bodyParserF, Parser<G> bodyParserG
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple7<>(
                    bodyParserA.run(reader),
                    bodyParserB.run(reader),
                    bodyParserC.run(reader),
                    bodyParserD.run(reader),
                    bodyParserE.run(reader),
                    bodyParserF.run(reader),
                    bodyParserG.run(reader)
                );
            }
            pool.$1 = bodyParserA.run(reader, pool.$1);
            pool.$2 = bodyParserB.run(reader, pool.$2);
            pool.$3 = bodyParserC.run(reader, pool.$3);
            pool.$4 = bodyParserD.run(reader, pool.$4);
            pool.$5 = bodyParserE.run(reader, pool.$5);
            pool.$6 = bodyParserF.run(reader, pool.$6);
            pool.$7 = bodyParserG.run(reader, pool.$7);
            return pool;
        };
    }

    public static <A,B,C,D,E,F,G,H> Parser<Tuple8<A,B,C,D,E,F,G,H>> rawSeq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC,
            Parser<D> bodyParserD, Parser<E> bodyParserE, Parser<F> bodyParserF,
            Parser<G> bodyParserG, Parser<H> bodyParserH
    ) {
        return (reader, pool) -> {
            if (pool == null) {
                return new Tuple8<>(
                    bodyParserA.run(reader),
                    bodyParserB.run(reader),
                    bodyParserC.run(reader),
                    bodyParserD.run(reader),
                    bodyParserE.run(reader),
                    bodyParserF.run(reader),
                    bodyParserG.run(reader),
                    bodyParserH.run(reader)
                );
            }
            pool.$1 = bodyParserA.run(reader, pool.$1);
            pool.$2 = bodyParserB.run(reader, pool.$2);
            pool.$3 = bodyParserC.run(reader, pool.$3);
            pool.$4 = bodyParserD.run(reader, pool.$4);
            pool.$5 = bodyParserE.run(reader, pool.$5);
            pool.$6 = bodyParserF.run(reader, pool.$6);
            pool.$7 = bodyParserG.run(reader, pool.$7);
            pool.$8 = bodyParserH.run(reader, pool.$8);
            return pool;
        };
    }

    public static <A,B> PooledParser<Tuple2<A,B>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB
    ) {
        return PooledParser.from(rawSeq(
            bodyParserA.purify(), bodyParserB.purify()
        ));
    }

    public static <A,B,C> PooledParser<Tuple3<A,B,C>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC
    ) {
        return PooledParser.from(rawSeq(
            bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify()
        ));
    }

    public static <A,B,C,D> PooledParser<Tuple4<A,B,C,D>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD
    ) {
        return PooledParser.from(rawSeq(
            bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(), bodyParserD.purify()
        ));
    }

    public static <A,B,C,D,E> PooledParser<Tuple5<A,B,C,D,E>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
            Parser<E> bodyParserE
    ) {
        return PooledParser.from(rawSeq(
            bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(),
            bodyParserD.purify(), bodyParserE.purify()
        ));
    }

    public static <A,B,C,D,E,F> PooledParser<Tuple6<A,B,C,D,E,F>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
            Parser<E> bodyParserE, Parser<F> bodyParserF
    ) {
        return PooledParser.from(rawSeq(
            bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(),
            bodyParserD.purify(), bodyParserE.purify(), bodyParserF.purify()
        ));
    }

    public static <A,B,C,D,E,F,G> PooledParser<Tuple7<A,B,C,D,E,F,G>> seq(
        Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
        Parser<E> bodyParserE, Parser<F> bodyParserF, Parser<G> bodyParserG
    ) {
        return PooledParser.from(rawSeq(
            bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(), bodyParserD.purify(),
            bodyParserE.purify(), bodyParserF.purify(), bodyParserG.purify()
        ));
    }

    public static <A,B,C,D,E,F,G,H> PooledParser<Tuple8<A,B,C,D,E,F,G,H>> seq(
        Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
        Parser<E> bodyParserE, Parser<F> bodyParserF, Parser<G> bodyParserG, Parser<H> bodyParserH
    ) {
        return PooledParser.from(rawSeq(
            bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(), bodyParserD.purify(),
            bodyParserE.purify(), bodyParserF.purify(), bodyParserG.purify(), bodyParserH.purify()
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

    /**
     * Constructs a parser, which, for an element with given elemName,
     * reads and returns the text inside this element.
     *
     * @param name Element's name (qualified)
     */
    public static Parser<String> elemText(QName name) {
        return open(name).nextR(text).nextL(close(name));
    }

    /**
     * Constructs a parser, which, for an element with given elemName,
     * reads and returns the text inside this element.
     *
     * @param name Element's name (unqualified)
     */
    public static Parser<String> elemText(String name) {
        return open(name).nextR(text).nextL(close(name));
    }

    /**
     * Constructs a parser, which, for an element with given elemName,
     * reads an attribute attrName and text inside the element, and returns them as a tuple.
     *
     * @param elemName Element's name (qualified)
     * @param attrName Attribute's name (qualified)
     */
    public static Parser<Tuple2<String,String>> elemAttrAndText(QName elemName, QName attrName) {
        return within(elemName, attr(attrName), text);
    }

    /**
     * Constructs a parser, which, for an element with given elemName,
     * reads an attribute attrName and text inside the element, and returns them as a tuple.
     *
     * @param elemName Element's name (unqualified)
     * @param attrName Attribute's name (unqualified)
     */
    public static Parser<Tuple2<String,String>> elemAttrAndText(String elemName, String attrName) {
        return within(elemName, attr(attrName), text);
    }

    /**
     * Constructs a parser, which executes bodyParser inside the element with given name,
     * and returns the result of its execution.
     *
     * @param name Element's name (qualified)
     * @param bodyParser Parser to run inside the element
     * @param <B> Type of bodyParser's result
     */
    public static <B> Parser<B> within(QName name, Parser<B> bodyParser) {
        return open(name).nextR(bodyParser).nextL(close(name));
    }

    /**
     * Constructs a parser, which executes bodyParser inside the element with given name,
     * and returns the result of its execution.
     *
     * @param name Element's name (unqualified)
     * @param bodyParser Parser to run inside the element
     * @param <B> Type of bodyParser's result
     */
    public static <B> Parser<B> within(String name, Parser<B> bodyParser) {
        return open(name).nextR(bodyParser).nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParser inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParser Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <B> Type of bodyParser's result
     */
    public static <T,B> Parser<Tuple2<T,B>> within(
            QName name,
            Parser<T> targetParser,
            Parser<B> bodyParser
    ) {
        return
            seq(open(name, targetParser),
                bodyParser)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA and bodyParserB one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     */
    public static <T,A,B> Parser<Tuple3<T,A,B>> within(
            QName name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, and bodyParserC one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     */
    public static <T,A,B,C> Parser<Tuple4<T,A,B,C>> within(
            QName name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, bodyParserC,
     * and bodyParserD one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param bodyParserD fourth Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     * @param <D> Type of bodyParserD's result
     */
    public static <T,A,B,C,D> Parser<Tuple5<T,A,B,C,D>> within(
            QName name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC,
            Parser<D> bodyParserD
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC,
                bodyParserD)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, bodyParserC, bodyParserD,
     * and bodyParserE one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param bodyParserD fourth Parser to run inside the element
     * @param bodyParserE fifth Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     * @param <D> Type of bodyParserD's result
     * @param <E> Type of bodyParserE's result
     */
    public static <T,A,B,C,D,E> Parser<Tuple6<T,A,B,C,D,E>> within(
            QName name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC,
            Parser<D> bodyParserD,
            Parser<E> bodyParserE
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC,
                bodyParserD,
                bodyParserE)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, bodyParserC, bodyParserD, bodyParserE,
     * and bodyParserF one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param bodyParserD fourth Parser to run inside the element
     * @param bodyParserE fifth Parser to run inside the element
     * @param bodyParserF sixth Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     * @param <D> Type of bodyParserD's result
     * @param <E> Type of bodyParserE's result
     * @param <F> Type of bodyParserF's result
     */
    public static <T,A,B,C,D,E,F> Parser<Tuple7<T,A,B,C,D,E,F>> within(
            QName name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC,
            Parser<D> bodyParserD,
            Parser<E> bodyParserE,
            Parser<F> bodyParserF
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC,
                bodyParserD,
                bodyParserE,
                bodyParserF)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, bodyParserC, bodyParserD, bodyParserE, bodyParserF,
     * and bodyParserG one after another inside the element, and then returns corresponding results as a tuple.
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param bodyParserD fourth Parser to run inside the element
     * @param bodyParserE fifth Parser to run inside the element
     * @param bodyParserF sixth Parser to run inside the element
     * @param bodyParserG seventh Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     * @param <D> Type of bodyParserD's result
     * @param <E> Type of bodyParserE's result
     * @param <F> Type of bodyParserF's result
     * @param <G> Type of bodyParserG's result
     */
    public static <T,A,B,C,D,E,F,G> Parser<Tuple8<T,A,B,C,D,E,F,G>> within(
            QName name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC,
            Parser<D> bodyParserD,
            Parser<E> bodyParserE,
            Parser<F> bodyParserF,
            Parser<G> bodyParserG
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC,
                bodyParserD,
                bodyParserE,
                bodyParserF,
                bodyParserG)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParser inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParser Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <B> Type of bodyParser's result
     */
    public static <T,B> Parser<Tuple2<T, B>> within(
            String name,
            Parser<T> targetParser,
            Parser<B> bodyParser
    ) {
        return
            seq(open(name, targetParser),
                bodyParser)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA and bodyParserB one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     */
    public static <T,A,B> Parser<Tuple3<T,A,B>> within(
            String name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, and bodyParserC one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     */
    public static <T,A,B,C> Parser<Tuple4<T,A,B,C>> within(
            String name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, bodyParserC,
     * and bodyParserD one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param bodyParserD fourth Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     * @param <D> Type of bodyParserD's result
     */
    public static <T,A,B,C,D> Parser<Tuple5<T,A,B,C,D>> within(
            String name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC,
            Parser<D> bodyParserD
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC,
                bodyParserD)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, bodyParserC, bodyParserD,
     * and bodyParserE one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param bodyParserD fourth Parser to run inside the element
     * @param bodyParserE fifth Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     * @param <D> Type of bodyParserD's result
     * @param <E> Type of bodyParserE's result
     */
    public static <T,A,B,C,D,E> Parser<Tuple6<T,A,B,C,D,E>> within(
            String name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC,
            Parser<D> bodyParserD,
            Parser<E> bodyParserE
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC,
                bodyParserD,
                bodyParserE)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, bodyParserC, bodyParserD, bodyParserE,
     * and bodyParserF one after another inside the element,
     * and then returns corresponding results as a tuple.
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param bodyParserD fourth Parser to run inside the element
     * @param bodyParserE fifth Parser to run inside the element
     * @param bodyParserF sixth Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     * @param <D> Type of bodyParserD's result
     * @param <E> Type of bodyParserE's result
     * @param <F> Type of bodyParserF's result
     */
    public static <T,A,B,C,D,E,F> Parser<Tuple7<T,A,B,C,D,E,F>> within(
            String name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC,
            Parser<D> bodyParserD,
            Parser<E> bodyParserE,
            Parser<F> bodyParserF
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC,
                bodyParserD,
                bodyParserE,
                bodyParserF)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name,
     * then executes bodyParserA, bodyParserB, bodyParserC, bodyParserD, bodyParserE, bodyParserF,
     * and bodyParserG one after another inside the element, and then returns corresponding results as a tuple.
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParserA first Parser to run inside the element
     * @param bodyParserB second Parser to run inside the element
     * @param bodyParserC third Parser to run inside the element
     * @param bodyParserD fourth Parser to run inside the element
     * @param bodyParserE fifth Parser to run inside the element
     * @param bodyParserF sixth Parser to run inside the element
     * @param bodyParserG seventh Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <A> Type of bodyParserA's result
     * @param <B> Type of bodyParserB's result
     * @param <C> Type of bodyParserC's result
     * @param <D> Type of bodyParserD's result
     * @param <E> Type of bodyParserE's result
     * @param <F> Type of bodyParserF's result
     * @param <G> Type of bodyParserG's result
     */
    public static <T,A,B,C,D,E,F,G> Parser<Tuple8<T,A,B,C,D,E,F,G>> within(
            String name,
            Parser<T> targetParser,
            Parser<A> bodyParserA,
            Parser<B> bodyParserB,
            Parser<C> bodyParserC,
            Parser<D> bodyParserD,
            Parser<E> bodyParserE,
            Parser<F> bodyParserF,
            Parser<G> bodyParserG
    ) {
        return
            seq(open(name, targetParser),
                bodyParserA,
                bodyParserB,
                bodyParserC,
                bodyParserD,
                bodyParserE,
                bodyParserF,
                bodyParserG)
            .nextL(close(name));
    }

    /**
     * Constructs a parser, which repeatedly executes bodyParser inside the element until its end,
     * and returns a list of bodyParser's results.
     *
     * @param name Element's name (qualified)
     * @param bodyParser Parser to run inside the element
     * @param <B> Type of bodyParser's result
     */
    public static <B> Parser<List<B>> manyWithin(QName name, Parser<B> bodyParser) {
        return open(name).nextR(bodyParser.until(tryClose(name)));
    }

    /**
     * Constructs a parser, which repeatedly executes bodyParser inside the element until its end,
     * and returns a list of bodyParser's results.
     *
     * @param name Element's name (unqualified)
     * @param bodyParser Parser to run inside the element
     * @param <B> Type of bodyParser's result
     */
    public static <B> Parser<List<B>> manyWithin(String name, Parser<B> bodyParser) {
        return open(name).nextR(bodyParser.until(tryClose(name)));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name once,
     * then repeatedly executes bodyParser inside the element until its end,
     * then returns a tuple, consisting of targetParser's result, and a list of bodyParser's results
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParser Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <B> Type of bodyParser's result
     */
    public static <T,B> Parser<Tuple2<T,List<B>>> manyWithin(
            QName name, Parser<T> targetParser, Parser<B> bodyParser
    ) {
        return seq(opens(name).nextR(targetParser).nextL(step), bodyParser.until(tryClose(name)));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name once,
     * then repeatedly executes bodyParser inside the element until its end,
     * then returns a tuple, consisting of targetParser's result, and a list of bodyParser's results
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParser Parser to run inside the element
     * @param <T> Type of targetParser's result
     * @param <B> Type of bodyParser's result
     */
    public static <T,B> Parser<Tuple2<T,List<B>>> manyWithin(
            String name, Parser<T> targetParser, Parser<B> bodyParser
    ) {
        return seq(opens(name).nextR(targetParser).nextL(step), bodyParser.until(tryClose(name)));
    }

    /**
     * Constructs a parser, which repeatedly executes bodyParser inside the element until its end.
     *
     * @param name Element's name (qualified)
     * @param bodyParser Parser to run inside the element
     */
    public static Parser<?> evalManyWithin(QName name, Parser<?> bodyParser) {
        return open(name).nextL(bodyParser.until_(tryClose(name)));
    }

    /**
     * Constructs a parser, which repeatedly executes bodyParser inside the element until its end.
     *
     * @param name Element's name (unqualified)
     * @param bodyParser Parser to run inside the element
     */
    public static Parser<?> evalManyWithin(String name, Parser<?> bodyParser) {
        return open(name).nextL(bodyParser.until_(tryClose(name)));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name once,
     * then repeatedly executes bodyParser inside the element until its end,
     * then returns the targetParser's result.
     *
     * @param name Element's name (qualified)
     * @param targetParser Parser to run on the element
     * @param bodyParser Parser to run inside the element
     * @param <T> Type of targetParser's result
     */
    public static <T> Parser<T> evalManyWithin(
            QName name, Parser<T> targetParser, Parser<?> bodyParser
    ) {
        return opens(name).nextR(targetParser).nextL(step).nextL(bodyParser.until_(tryClose(name)));
    }

    /**
     * Constructs a parser, which executes targetParser on the element with given name once,
     * then repeatedly executes bodyParser inside the element until its end,
     * then returns the targetParser's result.
     *
     * @param name Element's name (unqualified)
     * @param targetParser Parser to run on the element
     * @param bodyParser Parser to run inside the element
     * @param <T> Type of targetParser's result
     */
    public static <T> Parser<T> evalManyWithin(
            String name, Parser<T> targetParser, Parser<?> bodyParser
    ) {
        return opens(name).nextR(targetParser).nextL(step).nextL(bodyParser.until_(tryClose(name)));
    }
}
