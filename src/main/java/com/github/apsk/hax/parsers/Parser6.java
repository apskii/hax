package com.github.apsk.hax.parsers;

import com.github.apsk.hax.Parser;
import com.github.apsk.j8t.Tuple6;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser6<A,B,C,D,E,F> extends Parser<Tuple6<A,B,C,D,E,F>> {
    default <G> Parser7<A,B,C,D,E,F,G> and(Parser<G> p) {
        return r -> {
            Tuple6<A,B,C,D,E,F> lhs = this.run(r);
            G rhs = p.run(r);
            return tuple(lhs.val1, lhs.val2, lhs.val3, lhs.val4, lhs.val5, lhs.val6, rhs);
        };
    }
}
