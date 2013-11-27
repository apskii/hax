package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.Tuple4;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser4<A,B,C,D> extends Parser<Tuple4<A,B,C,D>> {
    default <E> Parser5<A,B,C,D,E> and(Parser<E> p) {
        return r -> {
            Tuple4<A,B,C,D> lhs = this.run(r);
            E rhs = p.run(r);
            return tuple(lhs.$1, lhs.$2, lhs.$3, lhs.$4, rhs);
        };
    }
    default Parser4<A,B,C,D> nextL(Parser<?> p) {
        return r -> {
            Tuple4<A,B,C,D> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}
