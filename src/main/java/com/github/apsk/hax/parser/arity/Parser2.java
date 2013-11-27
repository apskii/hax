package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.Tuple2;

import javax.xml.stream.XMLStreamException;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser2<A,B> extends Parser<Tuple2<A,B>> {
    default <C> Parser3<A,B,C> and(Parser<C> p) throws XMLStreamException {
        return r -> {
            Tuple2<A,B> lhs = this.run(r);
            C rhs = p.run(r);
            return tuple(lhs.$1, lhs.$2, rhs);
        };
    }
    default Parser2<A,B> nextL(Parser<?> p) {
        return r -> {
            Tuple2<A,B> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}
