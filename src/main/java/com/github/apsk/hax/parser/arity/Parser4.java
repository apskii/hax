package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.*;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser4<A,B,C,D> extends Parser<Tuple4<A,B,C,D>> {
    /*default <E> Parser5<A,B,C,D,E> and(Parser<E> p) {
        return r -> {
            Tuple4<A,B,C,D> lhs = this.run(r);
            E rhs = p.run(r);
            return tuple(lhs.$1, lhs.$2, lhs.$3, lhs.$4, rhs);
        };
    }
    default <E,F> Parser6<A,B,C,D,E,F> and(Parser2<E,F> p) {
        return r -> {
            Tuple4<A,B,C,D> lhs = this.run(r);
            Tuple2<E,F> rhs = p.run(r);
            return new Tuple6<>(lhs.$1, lhs.$2, lhs.$3, lhs.$4, rhs.$1, rhs.$2);
        };
    }
    default <E,F,G> Parser7<A,B,C,D,E,F,G> and(Parser3<E,F,G> p) {
        return r -> {
            Tuple4<A,B,C,D> lhs = this.run(r);
            Tuple3<E,F,G> rhs = p.run(r);
            return new Tuple7<>(lhs.$1, lhs.$2, lhs.$3, lhs.$4, rhs.$1, rhs.$2, rhs.$3);
        };
    }
    default <E,F,G,H> Parser8<A,B,C,D,E,F,G,H> and(Parser4<E,F,G,H> p) {
        return r -> {
            Tuple4<A,B,C,D> lhs = this.run(r);
            Tuple4<E,F,G,H> rhs = p.run(r);
            return new Tuple8<>(lhs.$1, lhs.$2, lhs.$3, lhs.$4, rhs.$1, rhs.$2, rhs.$3, rhs.$4);
        };
    }*/
    default Parser4<A,B,C,D> nextL(Parser<?> p) {
        return r -> {
            Tuple4<A,B,C,D> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}
