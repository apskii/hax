package com.github.apsk.hax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface Parser<R> {
    /**
     * Runs the parser with the given reader and pool, and returns its result.
     * <p>When the pool is not null, parser can store result in it, but that's not a requirement.
     *
     * @param reader Reader to get data from
     * @param pool Pool to store result in
     * @return Result of parser execution
     * @throws XMLStreamException
     */
    R run(XMLStreamReader reader, R pool) throws XMLStreamException;

    /**
     * Runs the parser with the given reader, and returns its result.
     *
     * @param reader Reader to get data from
     * @return Result of parser execution
     * @throws XMLStreamException
     */
    default R run(XMLStreamReader reader) throws XMLStreamException {
        return this.run(reader, null);
    }

    /**
     * Constructs a parser, which is equivalent to this,
     * except its result is postprocessed with the function f.
     *
     * @param f Function to process parser's result
     * @param <X> f's codomain type
     */
    default <X> Parser<X> map(Function<R,X> f) {
        return (reader, _pool) -> f.apply(this.run(reader));
    }

    /**
     * Constructs a parser, which is equivalent to this, except its result
     * is postprocessed with the given effectful computation and discarded.
     *
     * @param computation Effectful computation to run on result
     */
    default Parser<?> effect(Consumer<R> computation) {
        return (reader, _pool) -> {
            computation.accept(this.run(reader));
            return null;
        };
    }

    /**
     * Constructs a parser, which repeatedly executes this parser and collects results into a list,
     * until the execution of predicateParser returns true.

     * @param predicateParser Parser to check termination predicate
     */
    default SelfPooledParser<List<R>> until(Parser<Boolean> predicateParser) {
        Parser<List<R>> listParser = (reader, pool) -> {
            if (pool != null) {
                int ix = 0;
                int lastIndex = pool.size() - 1;
                while (!predicateParser.run(reader)) {
                    if (ix <= lastIndex) {
                        pool.set(ix, this.run(reader, pool.get(ix)));
                    } else {
                        pool.add(this.run(reader));
                        lastIndex += 1;
                    }
                    ix += 1;
                }
                if (ix <= lastIndex) {
                    pool.subList(ix, lastIndex).clear();
                }
                return pool;
            } else {
                List<R> xs = new ArrayList<>();
                while (!predicateParser.run(reader)) {
                    xs.add(this.run(reader));
                }
                return xs;
            }
        };
        return SelfPooledParser.from(listParser);
    }

    /**
     *
     * @param pred
     * @return
     */
    default Parser<?> until_(Parser<Boolean> pred) {
        return (reader, _pool) -> {
            while (!pred.run(reader))
                this.run(reader);
            return null;
        };
    }

    /**
     *
     * @param otherParser
     * @return
     */
    default Parser<R> nextL(Parser<?> otherParser) {
        return (reader, pool) -> {
            R result = this.run(reader, pool);
            otherParser.run(reader);
            return result;
        };
    }

    /**
     *
     * @param otherParser
     * @param <X>
     * @return
     */
    default <X> Parser<X> nextR(Parser<X> otherParser) {
        return (reader, _pool) -> {
            this.run(reader);
            return otherParser.run(reader, _pool);
        };
    }

    /**
     *
     * @param otherParser
     * @param <X>
     * @return
     */
    default <X> SelfPooledParser<X> nextR(SelfPooledParser<X> otherParser) {
        Parser<X> pureParser = otherParser.parser;
        return SelfPooledParser.from((reader, pool) -> {
            this.run(reader);
            return pureParser.run(reader, pool);
        });
    }

    /**
     * Removes self-pooling from the parser, if any.
     *
     * @return Pure parser
     */
    default Parser<R> purify() {
        return this;
    }
}
