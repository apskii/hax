package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.Tuple7;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser7<A,B,C,D,E,F,G> extends Parser<Tuple7<A,B,C,D,E,F,G>> {
    /*default <H> Parser8<A,B,C,D,E,F,G,H> and(Parser<H> p) {
        return r -> {
            Tuple7<A,B,C,D,E,F,G> lhs = this.run(r);
            H rhs = p.run(r);
            return tuple(lhs.$1, lhs.$2, lhs.$3, lhs.$4, lhs.$5, lhs.$6, lhs.$7, rhs);
        };
    }*/
    default Parser7<A,B,C,D,E,F,G> nextL(Parser<?> p) {
        return (reader, pool) -> {
            Tuple7<A,B,C,D,E,F,G> result = this.run(reader, pool);
            p.run(reader);
            return result;
        };
    }
}
