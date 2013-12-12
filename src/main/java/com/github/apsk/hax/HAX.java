package com.github.apsk.hax;

import com.github.apsk.j8t.*;

import javax.xml.namespace.QName;
import java.util.List;

public final class HAX {
    /**
     * Constructs a parser, which skips all spaces.
     */
    public static Parser<?> skipSpaces = (reader, _pool) -> {
        while (reader.isCharacters() && reader.isWhiteSpace()) {
            reader.next();
        }
        return null;
    };

    /**
     * Constructs a parser, which moves reader one position forward and then skips all spaces.
     */
    public static Parser<?> step = (reader, _pool) -> {
        reader.next();
        skipSpaces.run(reader);
        return null;
    };

    /**
     * Constructs a parser, which skips everything until reaches an opening element elemName.
     *
     * @param elemName Element's name (qualified)
     */
    public static Parser<?> skipTo(QName elemName) {
        return (reader, _pool) -> {
            for (;;) {
                if (reader.isStartElement()) {
                    QName eventElemName = reader.getName();
                    if (eventElemName.equals(elemName))
                        return null;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                        "`skipTo(" + elemName + ")` reached the end of stream.");
                }
                reader.next();
            }
        };
    }

    /**
     * Constructs a parser, which skips everything until reaches an opening element elemName.
     *
     * @param elemName Element's name (unqualified)
     */
    public static Parser<?> skipTo(String elemName) {
        return (reader, _pool) -> {
            for (;;) {
                if (reader.isStartElement()) {
                    if (reader.getLocalName().equals(elemName))
                        return null;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                        "`skipTo(" + elemName + ")` reached the end of stream.");
                }
                reader.next();
            }
        };
    }

    /**
     * Constructs a parser, which ensures that the current element is opening element,
     * its name is elemName, and then puts the reader's cursor after it.
     *
     * @param elemName Element's name (qualified)
     */
    public static Parser<?> open(QName elemName) {
        return (reader, _pool) -> {
            if (reader.isStartElement()) {
                QName eventElemName = reader.getName();
                if (eventElemName.equals(elemName)) {
                    reader.next();
                    skipSpaces.run(reader);
                    return null;
                } else {
                    throw new ParserException(
                        "`open(" + elemName + ")` called on wrong opening element.");
                }
            } else {
                throw new ParserException(
                    "`open(" + elemName + ")` called on non-opening element.");
            }
        };
    }

    /**
     * Constructs a parser, which ensures that the current element is opening element,
     * its name is elemName, and then puts the reader's cursor after it.
     *
     * @param elemName Element's name (unqualified)
     */
    public static Parser<?> open(String elemName) {
        return (reader, _pool) -> {
            if (reader.isStartElement()) {
                if (reader.getLocalName().equals(elemName)) {
                    reader.next();
                    skipSpaces.run(reader);
                    return null;
                } else {
                    throw new ParserException(
                        "`open(" + elemName + ")` called on wrong opening element.");
                }
            } else {
                throw new ParserException(
                    "`open(" + elemName + ")` called on non-opening element.");
            }
        };
    }

    /**
     * Constructs a parser, which ensures that the current element is opening element and its name is elemName.
     *
     * @param elemName Element's name (qualified)
     */
    public static Parser<?> opens(QName elemName) {
        return (reader, _pool) -> {
            if (reader.isStartElement()) {
                if (reader.getName().equals(elemName))
                    return null;
                else {
                    throw new ParserException(
                        "`opens(" + elemName + ")` called on wrong opening element.");
                }
            } else {
                throw new ParserException(
                    "`opens(" + elemName + ")` called on non-opening element.");
            }
        };
    }

    /**
     * Constructs a parser, which ensures that the current element is opening element and its name is elemName.
     *
     * @param elemName Element's name (unqualified)
     */
    public static Parser<?> opens(String elemName) {
        return (reader, _pool) -> {
            if (reader.isStartElement()) {
                if (reader.getLocalName().equals(elemName))
                    return null;
                else {
                    throw new ParserException(
                        "`opens(" + elemName + ")` called on wrong opening element.");
                }
            } else {
                throw new ParserException(
                    "`opens(" + elemName + ")` called on non-opening element.");
            }
        };
    }

    /**
     * Constructs a parser, which ensures that the current element is closing element,
     * its name is elemName, and then puts the reader's cursor after it.
     *
     * @param elemName Element's name (qualified)
     */
    public static Parser<?> close(QName elemName) {
        return (reader, _pool) -> {
            if (reader.isEndElement()) {
                if (reader.getName().equals(elemName)) {
                    reader.next();
                    return null;
                } else {
                    throw new ParserException(
                        "`close(" + elemName + ")` called on wrong closing element.");
                }
            } else {
                throw new ParserException(
                    "`close(" + elemName + ")` called on non-closing element.");
            }
        };
    }

    /**
     * Constructs a parser, which ensures that the current element is closing element,
     * its name is elemName, and then puts the reader's cursor after it.
     *
     * @param elemName Element's name (unqualified)
     */
    public static Parser<?> close(String elemName) {
        return (reader, _pool) -> {
            if (reader.isEndElement()) {
                if (reader.getLocalName().equals(elemName)) {
                    reader.next();
                    return null;
                } else {
                    throw new ParserException(
                        "`close(" + elemName + ")` called on wrong closing element.");
                }
            } else {
                throw new ParserException(
                    "`close(" + elemName + ")` called on non-closing element.");
            }
        };
    }

    /**
     * Constructs a parser, which checks, whether the current element is closing element
     * and its name is elemName, and then returns false if it's not, otherwise
     * puts the reader's cursor after it and returns true.
     *
     * @param elemName Element's name (qualified)
     */
    public static Parser<Boolean> tryClose(QName elemName) {
        return (reader, _pool) -> {
            for (;;) {
                if (reader.isEndElement()) {
                    if (reader.getName().equals(elemName)) {
                        reader.next();
                        skipSpaces.run(reader);
                        return true;
                    }
                } else if (!reader.isCharacters() || !reader.isWhiteSpace()) {
                    return false;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                        "`tryClose(" + elemName + ")` called at the end of stream.");
                }
                reader.next();
            }
        };
    }

    /**
     * Constructs a parser, which checks, whether the current element is closing element
     * and its name is elemName, and then returns false if it's not, otherwise
     * puts the reader's cursor after it and returns true.
     *
     * @param elemName Element's name (unqualified)
     */
    public static Parser<Boolean> tryClose(String elemName) {
        return (reader, _pool) -> {
            for (;;) {
                if (reader.isEndElement()) {
                    if (reader.getLocalName().equals(elemName)) {
                        reader.next();
                        skipSpaces.run(reader);
                        return true;
                    }
                } else if (!reader.isCharacters() || !reader.isWhiteSpace()) {
                    return false;
                }
                if (!reader.hasNext()) {
                    throw new ParserException(
                            "`tryClose(" + elemName + ")` called at the end of stream.");
                }
                reader.next();
            }
        };
    }

    /**
     * Constructs a parser, which returns a boolean value indicating
     * whether the current element is ending element and its name is elemName.
     *
     * @param elemName Element's name (qualified)
     */
    public static Parser<Boolean> closing(QName elemName) {
        return (reader, _pool) -> {
            if (reader.isEndElement()) {
                if (reader.getName().equals(elemName))
                    return true;
            }
            return false;
        };
    }

    /**
     * Constructs a parser, which returns a boolean value indicating
     * whether the current element is ending element and its name is elemName.
     *
     * @param elemName Element's name (unqualified)
     */
    public static Parser<Boolean> closing(String elemName) {
        return (reader, _pool) -> {
            if (reader.isEndElement()) {
                if (reader.getLocalName().equals(elemName))
                    return true;
            }
            return false;
        };
    }

    /**
     * Constructs a parser, which reads and returns the text
     * at the current position and puts the reader's cursor after its end.
     */
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

    /**
     * Constructs a parser, which reads and returns the value
     * of the attribute attrName of the current element.
     *
     * @param name Attribute's name (qualified)
     */
    public static Parser<String> attr(QName name) {
        return (reader, _pool) -> {
            if (!reader.isStartElement()) {
                throw new ParserException(
                    "`attr(" + name + ")` called at non-opening element.");
            }
            return reader.getAttributeValue(name.getNamespaceURI(), name.getLocalPart());
        };
    }

    /**
     * Constructs a parser, which reads and returns the value
     * of the attribute attrName of the current element.
     *
     * @param name Attribute's name (unqualified)
     */
    public static Parser<String> attr(String name) {
        return (reader, _pool) -> {
            if (!reader.isStartElement()) {
                throw new ParserException(
                    "`attr(" + name + ")` called at non-opening element.");
            }
            return reader.getAttributeValue(null, name);
        };
    }

    /**
     * Constructs a parser, which executes bodyParserA and bodyParserB
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     */
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

    /**
     * Constructs a parser, which executes bodyParserA, bodyParserB, and bodyParserC
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     */
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

    /**
     * Constructs a parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, and bodyParserD one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     */
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

    /**
     * Constructs a parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, bodyParserD, and bodyParserE one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param bodyParserE Fifth parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     * @param <E> Fifth parser's result type
     */
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

    /**
     * Constructs a parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, bodyParserD, bodyParserE, and bodyParserF
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param bodyParserE Fifth parser to execute
     * @param bodyParserF Sixth parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     * @param <E> Fifth parser's result type
     * @param <F> Sixth parser's result type
     */
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

    /**
     * Constructs a parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, bodyParserD, bodyParserE, bodyParserF, and bodyParserG
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param bodyParserE Fifth parser to execute
     * @param bodyParserF Sixth parser to execute
     * @param bodyParserG Seventh parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     * @param <E> Fifth parser's result type
     * @param <F> Sixth parser's result type
     * @param <G> Seventh parser's result type
     */
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

    /**
     * Constructs a parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, bodyParserD, bodyParserE, bodyParserF, bodyParserG, and bodyParserH
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param bodyParserE Fifth parser to execute
     * @param bodyParserF Sixth parser to execute
     * @param bodyParserG Seventh parser to execute
     * @param bodyParserH Eighth parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     * @param <E> Fifth parser's result type
     * @param <F> Sixth parser's result type
     * @param <G> Seventh parser's result type
     * @param <H> Eighth parser's result type
     */
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

    /**
     * Constructs a self-pooled parser, which executes bodyParserA and bodyParserB
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     */
    public static <A,B> SelfPooledParser<Tuple2<A,B>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB
    ) {
        return SelfPooledParser.from(rawSeq(
                bodyParserA.purify(), bodyParserB.purify()
        ));
    }

    /**
     * Constructs a self-pooled parser, which executes bodyParserA, bodyParserB, and bodyParserC
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     */
    public static <A,B,C> SelfPooledParser<Tuple3<A,B,C>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC
    ) {
        return SelfPooledParser.from(rawSeq(
                bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify()
        ));
    }

    /**
     * Constructs a self-pooled parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, and bodyParserD one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     */
    public static <A,B,C,D> SelfPooledParser<Tuple4<A,B,C,D>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD
    ) {
        return SelfPooledParser.from(rawSeq(
                bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(), bodyParserD.purify()
        ));
    }

    /**
     * Constructs a self-pooled parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, bodyParserD, and bodyParserE one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param bodyParserE Fifth parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     * @param <E> Fifth parser's result type
     */
    public static <A,B,C,D,E> SelfPooledParser<Tuple5<A,B,C,D,E>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
            Parser<E> bodyParserE
    ) {
        return SelfPooledParser.from(rawSeq(
                bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(),
                bodyParserD.purify(), bodyParserE.purify()
        ));
    }

    /**
     * Constructs a self-pooled parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, bodyParserD, bodyParserE, and bodyParserF
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param bodyParserE Fifth parser to execute
     * @param bodyParserF Sixth parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     * @param <E> Fifth parser's result type
     * @param <F> Sixth parser's result type
     */
    public static <A,B,C,D,E,F> SelfPooledParser<Tuple6<A,B,C,D,E,F>> seq(
            Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
            Parser<E> bodyParserE, Parser<F> bodyParserF
    ) {
        return SelfPooledParser.from(rawSeq(
                bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(),
                bodyParserD.purify(), bodyParserE.purify(), bodyParserF.purify()
        ));
    }

    /**
     * Constructs a self-pooled parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, bodyParserD, bodyParserE, bodyParserF, and bodyParserG
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param bodyParserE Fifth parser to execute
     * @param bodyParserF Sixth parser to execute
     * @param bodyParserG Seventh parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     * @param <E> Fifth parser's result type
     * @param <F> Sixth parser's result type
     * @param <G> Seventh parser's result type
     */
    public static <A,B,C,D,E,F,G> SelfPooledParser<Tuple7<A,B,C,D,E,F,G>> seq(
        Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
        Parser<E> bodyParserE, Parser<F> bodyParserF, Parser<G> bodyParserG
    ) {
        return SelfPooledParser.from(rawSeq(
                bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(), bodyParserD.purify(),
                bodyParserE.purify(), bodyParserF.purify(), bodyParserG.purify()
        ));
    }

    /**
     * Constructs a self-pooled parser, which executes bodyParserA, bodyParserB,
     * bodyParserC, bodyParserD, bodyParserE, bodyParserF, bodyParserG, and bodyParserH
     * one after another, and then returns their results.
     *
     * @param bodyParserA First parser to execute
     * @param bodyParserB Second parser to execute
     * @param bodyParserC Third parser to execute
     * @param bodyParserD Fourth parser to execute
     * @param bodyParserE Fifth parser to execute
     * @param bodyParserF Sixth parser to execute
     * @param bodyParserG Seventh parser to execute
     * @param bodyParserH Eighth parser to execute
     * @param <A> First parser's result type
     * @param <B> Second parser's result type
     * @param <C> Third parser's result type
     * @param <D> Fourth parser's result type
     * @param <E> Fifth parser's result type
     * @param <F> Sixth parser's result type
     * @param <G> Seventh parser's result type
     * @param <H> Eighth parser's result type
     */
    public static <A,B,C,D,E,F,G,H> SelfPooledParser<Tuple8<A,B,C,D,E,F,G,H>> seq(
        Parser<A> bodyParserA, Parser<B> bodyParserB, Parser<C> bodyParserC, Parser<D> bodyParserD,
        Parser<E> bodyParserE, Parser<F> bodyParserF, Parser<G> bodyParserG, Parser<H> bodyParserH
    ) {
        return SelfPooledParser.from(rawSeq(
                bodyParserA.purify(), bodyParserB.purify(), bodyParserC.purify(), bodyParserD.purify(),
                bodyParserE.purify(), bodyParserF.purify(), bodyParserG.purify(), bodyParserH.purify()
        ));
    }

    /**
     * Constructs a parser, which ensures that current element is opening element,
     * its name is elemName, then executes elemParser on it, puts the reader's cursor after it,
     * and returns the result of elemParser's execution.
     *
     * <p>
     *     Example for {@code '<x:items count="7">'}:
     *     <pre>open(new QName("x", "items"), attr("count"))
     *
     * @param elemName Element's name (qualified)
     * @param elemParser Parser to execute on element
     * @param <T> Type of elemParser's result
     */
    public static <T> Parser<T> open(QName elemName, Parser<T> elemParser) {
        return opens(elemName).nextR(elemParser).nextL(step);
    }

    /**
     * Constructs a parser, which ensures that current element is opening element,
     * its name is elemName, then executes elemParser on it, puts the reader's cursor after it,
     * and returns the result of elemParser's execution.
     *
     * <p>
     *     Example for {@code '<items count="7">'}:
     *     <pre>open("items", attr("count"))
     *
     * @param elemName Element's name (qualified)
     * @param elemParser Parser to execute on element
     * @param <T> Type of elemParser's result
     */
    public static <T> Parser<T> open(String elemName, Parser<T> elemParser) {
        return opens(elemName).nextR(elemParser).nextL(step);
    }

    /**
     * Constructs a parser, which ensures that current element is opening element,
     * its name is elemName, then executes elemParser on it, ensures next element is
     * corresponding closing element, puts the reader's cursor after the closing element,
     * and returns the result of elemParser's execution.
     *
     * <p>
     *     Example for {@code '<ns:language name="Java" version="8"></ns:language>'}:
     *     <pre>elem(new QName("ns", "language"), seq(attr("name"), attr("version")))
     *
     * @param elemName Element's name (qualified)
     * @param elemParser Parser to execute on element
     * @param <T> Type of elemParser's result
     */
    public static <T> Parser<T> elem(QName elemName, Parser<T> elemParser) {
        return opens(elemName).nextR(elemParser).nextL(step).nextL(close(elemName));
    }

    /**
     * Constructs a parser which ensures that current element is opening element,
     * its name is elemName, then executes elemParser on it, ensures next element is
     * corresponding closing element, puts the reader's cursor after the closing element,
     * and returns the result of elemParser's execution.
     *
     * <p>
     *     Example for {@code '<language name="Java" version="8"></language>'}:
     *     <pre>elem("language", seq(attr("name"), attr("version")))
     *
     * @param elemName Element's name (unqualified)
     * @param elemParser Parser to execute on element
     * @param <T> Type of elemParser's result
     */
    public static <T> Parser<T> elem(String elemName, Parser<T> elemParser) {
        return opens(elemName).nextR(elemParser).nextL(step).nextL(close(elemName));
    }

    /**
     * Constructs a parser which ensures that current element is opening element,
     * its name is elemName, then reads the value of the attribute attrName of this element,
     * ensures next element is corresponding closing element,
     * puts the reader's cursor after the closing element, and returns attribute's value read.
     *
     * <p>
     *     Example for {@code '<ns:language p:name="Java"></ns:language>'}:
     *     <pre>elemAttr(new QName("ns", "language"), new QName("p", "name"))
     *
     * @param elemName Element's name (qualified)
     * @param attrName Attribute's name (qualified)
     */
    public static Parser<String> elemAttr(QName elemName, QName attrName) {
        return opens(elemName).nextR(attr(attrName)).nextL(step).nextL(close(elemName));
    }

    /**
     * Constructs a parser which ensures that current element is opening element,
     * its name is elemName, then reads the value of the attribute attrName of this element,
     * ensures next element is corresponding closing element,
     * puts the reader's cursor after the closing element, and returns attribute's value read.
     *
     * <p>
     *     Example for {@code '<language name="Java"></language>'}:
     *     <pre>elemAttr("language", "name")
     *
     * @param elemName Element's name (unqualified)
     * @param attrName Attribute's name (unqualified)
     */
    public static Parser<String> elemAttr(String elemName, String attrName) {
        return opens(elemName).nextR(attr(attrName)).nextL(step).nextL(close(elemName));
    }

    /**
     * Constructs a parser which ensures that current element is opening element,
     * its name is elemName, then reads the text inside this element,
     * ensures next element is corresponding closing element,
     * puts the reader's cursor after the closing element, and returns text read.
     *
     * <p>
     *     Example for {@code '<w:spell>abracadabra</w:spell>'}:
     *     <pre>elemText(new QName("w", "spell"))
     *
     * @param name Element's name (qualified)
     */
    public static Parser<String> elemText(QName name) {
        return open(name).nextR(text).nextL(close(name));
    }

    /**
     * Constructs a parser which ensures that current element is opening element,
     * its name is elemName, then reads the text inside this element,
     * ensures next element is corresponding closing element,
     * puts the reader's cursor after the closing element, and returns text read.
     *
     * <p>
     *     Example for {@code '<spell>abracadabra</spell>'}:
     *     <pre>elemText("spell")
     *
     * @param name Element's name (unqualified)
     */
    public static Parser<String> elemText(String name) {
        return open(name).nextR(text).nextL(close(name));
    }

    /**
     * Constructs a parser which ensures that current element is opening element,
     * its name is elemName, then reads the value of the attribute attrName of this element,
     * then reads the text inside this element, ensures next element is corresponding closing element,
     * puts the reader's cursor after the closing element, and returns attribute's value and text read.
     *
     * <p>
     *     Example for {@code '<w:spell a:type="charm">ascendio</w:spell>'}:
     *     <pre>elemAttrAndText(new QName("w", "spell"), new QName("a", "type"))
     *
     * @param elemName Element's name (qualified)
     * @param attrName Attribute's name (qualified)
     */
    public static Parser<Tuple2<String,String>> elemAttrAndText(QName elemName, QName attrName) {
        return within(elemName, attr(attrName), text);
    }

    /**
     * Constructs a parser which ensures that current element is opening element,
     * its name is elemName, then reads the value of the attribute attrName of this element,
     * then reads the text inside this element, ensures next element is corresponding closing element,
     * puts the reader's cursor after the closing element, and returns attribute's value and text read.
     *
     * <p>
     *     Example for {@code '<spell type="charm">ascendio</spell>'}:
     *     <pre>elemAttrAndText("spell", "type")
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and bodyParserG one after another inside the element, and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and then returns their results.
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
     * and bodyParserG one after another inside the element, and then returns their results.
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
