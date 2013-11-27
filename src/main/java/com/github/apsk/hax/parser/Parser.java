package com.github.apsk.hax.parser;

import com.github.apsk.hax.parser.arity.Parser1;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface Parser<R> {
    R run(HAXEventReader eventReader) throws XMLStreamException;

    default <X> Parser<X> map(Function<R,X> f) {
        return r -> f.apply(this.run(r));
    }
    default Parser1<List<R>> until(Parser<Boolean> pred) {
        return r -> {
            List<R> xs = new ArrayList<>();
            for (;;) {
                if (pred.run(r)) return xs;
                xs.add(this.run(r));
            }
        };
    }
    default <X> Parser1<X> nextR(Parser<X> p) {
        return r -> {
            this.run(r);
            return p.run(r);
        };
    }
    default Parser1<R> merge() {
        return this::run;
    }
}
