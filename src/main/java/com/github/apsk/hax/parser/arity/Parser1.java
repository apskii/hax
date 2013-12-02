package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.*;

@FunctionalInterface
public interface Parser1<A> extends Parser<A> {
    /*default <B> Parser2<A,B> and(Parser<B> p) {
        return r -> new Tuple2<>(this.run(r), p.run(r));
    }
    default <B,C> Parser3<A,B,C> and(Parser2<B,C> p) {
        return r -> {
            A lhs = this.run(r);
            Tuple2<B,C> rhs = p.run(r);
            return new Tuple3<>(lhs, rhs.$1, rhs.$2);
        };
    }
    default <B,C,D> Parser4<A,B,C,D> and(Parser3<B,C,D> p) {
        return r -> {
            A lhs = this.run(r);
            Tuple3<B,C,D> rhs = p.run(r);
            return new Tuple4<>(lhs, rhs.$1, rhs.$2, rhs.$3);
        };
    }
    default <B,C,D,E> Parser5<A,B,C,D,E> and(Parser4<B,C,D,E> p) {
        return r -> {
            A lhs = this.run(r);
            Tuple4<B,C,D,E> rhs = p.run(r);
            return new Tuple5<>(lhs, rhs.$1, rhs.$2, rhs.$3, rhs.$4);
        };
    }
    default <B,C,D,E,F> Parser6<A,B,C,D,E,F> and(Parser5<B,C,D,E,F> p) {
        return r -> {
            A lhs = this.run(r);
            Tuple5<B,C,D,E,F> rhs = p.run(r);
            return new Tuple6<>(lhs, rhs.$1, rhs.$2, rhs.$3, rhs.$4, rhs.$5);
        };
    }
    default <B,C,D,E,F,G> Parser7<A,B,C,D,E,F,G> and(Parser6<B,C,D,E,F,G> p) {
        return r -> {
            A lhs = this.run(r);
            Tuple6<B,C,D,E,F,G> rhs = p.run(r);
            return new Tuple7<>(lhs, rhs.$1, rhs.$2, rhs.$3, rhs.$4, rhs.$5, rhs.$6);
        };
    }
    default <B,C,D,E,F,G,H> Parser8<A,B,C,D,E,F,G,H> and(Parser7<B,C,D,E,F,G,H> p) {
        return r -> {
            A lhs = this.run(r);
            Tuple7<B,C,D,E,F,G,H> rhs = p.run(r);
            return new Tuple8<>(lhs, rhs.$1, rhs.$2, rhs.$3, rhs.$4, rhs.$5, rhs.$6, rhs.$7);
        };
    }*/
    default Parser1<A> nextL(Parser<?> p) {
        return r -> {
            A result = this.run(r);
            p.run(r);
            return result;
        };
    }
}
