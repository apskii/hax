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
     * Runs the parser with given reader and pool, and returns its result.
     * <p>When pool is not null, parser could store result in it, but that's not a requirement.
     *
     * @param reader Reader to get data from
     * @param pool Pool to store result in
     * @return Result of parser execution
     * @throws XMLStreamException
     */
    R run(XMLStreamReader reader, R pool) throws XMLStreamException;

    /**
     * Runs the parser with given reader, and returns its result.
     *
     * @param reader Reader to get data from
     * @return Result of parser execution
     * @throws XMLStreamException
     */
    default R run(XMLStreamReader reader) throws XMLStreamException {
        return this.run(reader, null);
    }

    /**
     * Transforms the parser into a new parser with result mapped by f.
     *
     * @param f Mapper for the parser's result
     * @param <X> Mapper's codomain type
     */
    default <X> Parser<X> map(Function<R,X> f) {
        return (reader, _pool) -> f.apply(this.run(reader));
    }

    default Parser<?> effect(Consumer<R> f) {
        return (reader, _pool) -> {
            f.accept(this.run(reader));
            return null;
        };
    }

    default PooledParser<List<R>> until(Parser<Boolean> pred) {
        Parser<List<R>> listParser = (reader, pool) -> {
            if (pool != null) {
                int ix = 0;
                int lastIndex = pool.size() - 1;
                while (!pred.run(reader)) {
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
                while (!pred.run(reader)) {
                    xs.add(this.run(reader));
                }
                return xs;
            }
        };
        return PooledParser.from(listParser);
    }

    default Parser<?> until_(Parser<Boolean> pred) {
        return (reader, _pool) -> {
            while (!pred.run(reader))
                this.run(reader);
            return null;
        };
    }

    default Parser<R> nextL(Parser<?> otherParser) {
        return (reader, pool) -> {
            R result = this.run(reader, pool);
            otherParser.run(reader);
            return result;
        };
    }

    default <X> Parser<X> nextR(Parser<X> otherParser) {
        return (reader, _pool) -> {
            this.run(reader);
            return otherParser.run(reader, _pool);
        };
    }

    default <X> PooledParser<X> nextR(PooledParser<X> otherParser) {
        Parser<X> pureParser = otherParser.parser;
        return PooledParser.from((reader, pool) -> {
            this.run(reader);
            return pureParser.run(reader, pool);
        });
    }

    /**
     * Removes self-pooling from the parser, if any.
     * @return Pure parser
     */
    default Parser<R> purify() {
        return this;
    }
}
