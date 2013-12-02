package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.*;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser5<A,B,C,D,E> extends Parser<Tuple5<A,B,C,D,E>> {
    /*default <F> Parser6<A,B,C,D,E,F> and(Parser<F> p) {
        return r -> {
            Tuple5<A,B,C,D,E> lhs = this.run(r);
            F rhs = p.run(r);
            return tuple(lhs.$1, lhs.$2, lhs.$3, lhs.$4, lhs.$5, rhs);
        };
    }
    default <F,G> Parser7<A,B,C,D,E,F,G> and(Parser2<F,G> p) {
        return r -> {
            Tuple5<A,B,C,D,E> lhs = this.run(r);
            Tuple2<F,G> rhs = p.run(r);
            return new Tuple7<>(lhs.$1, lhs.$2, lhs.$3, lhs.$4, lhs.$5, rhs.$1, rhs.$2);
        };
    }
    default <F,G,H> Parser8<A,B,C,D,E,F,G,H> and(Parser3<F,G,H> p) {
        return r -> {
            Tuple5<A,B,C,D,E> lhs = this.run(r);
            Tuple3<F,G,H> rhs = p.run(r);
            return new Tuple8<>(lhs.$1, lhs.$2, lhs.$3, lhs.$4, lhs.$5, rhs.$1, rhs.$2, rhs.$3);
        };
    }*/
    default Parser5<A,B,C,D,E> nextL(Parser<?> p) {
        return r -> {
            Tuple5<A,B,C,D,E> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}