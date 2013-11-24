package com.github.apsk.hax.parsers;

import com.github.apsk.hax.Parser;
import com.github.apsk.j8t.Tuple7;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser7<A,B,C,D,E,F,G> extends Parser<Tuple7<A,B,C,D,E,F,G>> {
    default <H> Parser8<A,B,C,D,E,F,G,H> and(Parser<H> p) {
        return r -> {
            Tuple7<A,B,C,D,E,F,G> lhs = this.run(r);
            H rhs = p.run(r);
            return tuple(lhs.val1, lhs.val2, lhs.val3, lhs.val4, lhs.val5, lhs.val6, lhs.val7, rhs);
        };
    }
}
