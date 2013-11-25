package com.github.apsk.hax;

import com.github.apsk.hax.parsers.Parser1;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface Parser<R> {
    R run(XMLEventReader eventReader) throws XMLStreamException;
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
    default <X> Parser<R> nextL(Parser<X> p) {
        return r -> {
            R result = this.run(r);
            p.run(r);
            return result;
        };
    }
    default <X> Parser<X> nextR(Parser<X> p) {
        return r -> {
            this.run(r);
            return p.run(r);
        };
    }
}
