package com.github.apsk.hax;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface Parser<R> {
    R run(HAXEventReader eventReader, R pool) throws XMLStreamException;
    default R run(HAXEventReader eventReader) throws XMLStreamException {
        return this.run(eventReader, null);
    }
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
    default Parser<R> nextL(Parser<?> p) {
        return (reader, pool) -> {
            R result = this.run(reader, pool);
            p.run(reader);
            return result;
        };
    }
    default <X> Parser<X> nextR(Parser<X> pX) {
        return (reader, _pool) -> {
            this.run(reader);
            return pX.run(reader, _pool);
        };
    }
    default <X> PooledParser<X> nextR(PooledParser<X> pooledParser) {
        Parser<X> pureParser = pooledParser.parser;
        return PooledParser.from((reader, pool) -> {
            this.run(reader);
            return pureParser.run(reader, pool);
        });
    }
    default Parser<R> purify() {
        return this;
    }
}
