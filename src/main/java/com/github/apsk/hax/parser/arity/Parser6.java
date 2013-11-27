package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.*;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser6<A,B,C,D,E,F> extends Parser<Tuple6<A,B,C,D,E,F>> {
    default <G> Parser7<A,B,C,D,E,F,G> and(Parser<G> p) {
        return r -> {
            Tuple6<A,B,C,D,E,F> lhs = this.run(r);
            G rhs = p.run(r);
            return tuple(lhs.$1, lhs.$2, lhs.$3, lhs.$4, lhs.$5, lhs.$6, rhs);
        };
    }
    default <G,H> Parser8<A,B,C,D,E,F,G,H> and(Parser2<G,H> p) {
        return r -> {
            Tuple6<A,B,C,D,E,F> lhs = this.run(r);
            Tuple2<G,H> rhs = p.run(r);
            return new Tuple8<>(lhs.$1, lhs.$2, lhs.$3, lhs.$4, lhs.$5, lhs.$6, rhs.$1, rhs.$2);
        };
    }
    default Parser6<A,B,C,D,E,F> nextL(Parser<?> p) {
        return r -> {
            Tuple6<A,B,C,D,E,F> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}
