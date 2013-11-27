package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.Tuple3;

import javax.xml.stream.XMLStreamException;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser3<A,B,C> extends Parser<Tuple3<A,B,C>> {
    default <D> Parser4<A,B,C,D> and(Parser<D> p) throws XMLStreamException {
        return r -> {
            Tuple3<A,B,C> lhs = this.run(r);
            D rhs = p.run(r);
            return tuple(lhs.val1, lhs.val2, lhs.val3, rhs);
        };
    }
    default Parser3<A,B,C> nextL(Parser<?> p) {
        return r -> {
            Tuple3<A,B,C> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}
