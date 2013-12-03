package com.github.apsk.hax.parser;

import com.github.apsk.hax.parser.arity.Parser1;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    default Parser1<List<R>> until(Parser<Boolean> pred) {
        return (reader, pool) -> {
            List<R> xs = pool != null ? pool : new ArrayList<>();
            xs.clear();
            for (;;) {
                if (pred.run(reader)) return xs;
                xs.add(this.run(reader));
            }
        };
    }
    default Parser<?> until_(Parser<Boolean> pred) {
        return (reader, _pool) -> {
            while (!pred.run(reader))
                this.run(reader);
            return null;
        };
    }
    default Parser<Stream<R>> streamUntil(Parser<Boolean> pred) {
        return (reader, _pool) -> StreamSupport.stream(
            new ParserLoop<>(this, pred, reader), false);
    }
    default <X> Parser1<X> nextR(Parser<X> pX) {
        return (reader, _pool) -> {
            this.run(reader);
            return pX.run(reader, _pool);
        };
    }
    default Parser1<R> merge() {
        return this::run;
    }
    default Parser<R> pool() {
        class Ref { public R val = null; }
        Ref pool = new Ref();
        return (r, p) -> {
            if (p == null) {
                if (pool.val == null) {
                    pool.val = this.run(r);
                } else {
                    this.run(r, pool.val);
                }
                return pool.val;
            } else {
                return this.run(r, p);
            }
        };
    }
}
