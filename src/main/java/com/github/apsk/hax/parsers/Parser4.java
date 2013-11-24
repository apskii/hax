package com.github.apsk.hax.parsers;

import com.github.apsk.hax.Parser;
import com.github.apsk.j8t.Tuple4;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser4<A,B,C,D> extends Parser<Tuple4<A,B,C,D>> {
    default <E> Parser5<A,B,C,D,E> and(Parser<E> p) {
        return r -> {
            Tuple4<A,B,C,D> lhs = this.run(r);
            E rhs = p.run(r);
            return tuple(lhs.val1, lhs.val2, lhs.val3, lhs.val4, rhs);
        };
    }
}
