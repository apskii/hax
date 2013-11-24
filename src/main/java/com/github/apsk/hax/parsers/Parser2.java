package com.github.apsk.hax.parsers;

import com.github.apsk.hax.Parser;
import com.github.apsk.j8t.Tuple2;

import javax.xml.stream.XMLStreamException;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser2<A,B> extends Parser<Tuple2<A,B>> {
    default <C> Parser3<A,B,C> and(Parser<C> p) throws XMLStreamException {
        return r -> {
            Tuple2<A,B> lhs = this.run(r);
            C rhs = p.run(r);
            return tuple(lhs.val1, lhs.val2, rhs);
        };
    }
}
