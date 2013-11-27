package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.Tuple5;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser5<A,B,C,D,E> extends Parser<Tuple5<A,B,C,D,E>> {
    default <F> Parser6<A,B,C,D,E,F> and(Parser<F> p) {
        return r -> {
            Tuple5<A,B,C,D,E> lhs = this.run(r);
            F rhs = p.run(r);
            return tuple(lhs._1, lhs._2, lhs._3, lhs._4, lhs._5, rhs);
        };
    }
    default Parser5<A,B,C,D,E> nextL(Parser<?> p) {
        return r -> {
            Tuple5<A,B,C,D,E> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}