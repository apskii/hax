package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.*;

import javax.xml.stream.XMLStreamException;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser2<A,B> extends Parser<Tuple2<A,B>> {
    /*default <C> Parser3<A,B,C> and(Parser<C> p) throws XMLStreamException {
        return r -> {
            Tuple2<A,B> lhs = this.run(r);
            C rhs = p.run(r);
            return tuple(lhs.$1, lhs.$2, rhs);
        };
    }
    default <C,D> Parser4<A,B,C,D> and(Parser2<C,D> p) {
        return r -> {
            Tuple2<A,B> lhs = this.run(r);
            Tuple2<C,D> rhs = p.run(r);
            return new Tuple4<>(lhs.$1, lhs.$2, rhs.$1, rhs.$2);
        };
    }
    default <C,D,E> Parser5<A,B,C,D,E> and(Parser3<C,D,E> p) {
        return r -> {
            Tuple2<A,B> lhs = this.run(r);
            Tuple3<C,D,E> rhs = p.run(r);
            return new Tuple5<>(lhs.$1, lhs.$2, rhs.$1, rhs.$2, rhs.$3);
        };
    }
    default <C,D,E,F> Parser6<A,B,C,D,E,F> and(Parser4<C,D,E,F> p) {
        return r -> {
            Tuple2<A,B> lhs = this.run(r);
            Tuple4<C,D,E,F> rhs = p.run(r);
            return new Tuple6<>(lhs.$1, lhs.$2, rhs.$1, rhs.$2, rhs.$3, rhs.$4);
        };
    }
    default <C,D,E,F,G> Parser7<A,B,C,D,E,F,G> and(Parser5<C,D,E,F,G> p) {
        return r -> {
            Tuple2<A,B> lhs = this.run(r);
            Tuple5<C,D,E,F,G> rhs = p.run(r);
            return new Tuple7<>(lhs.$1, lhs.$2, rhs.$1, rhs.$2, rhs.$3, rhs.$4, rhs.$5);
        };
    }
    default <C,D,E,F,G,H> Parser8<A,B,C,D,E,F,G,H> and(Parser6<C,D,E,F,G,H> p) {
        return r -> {
            Tuple2<A,B> lhs = this.run(r);
            Tuple6<C,D,E,F,G,H> rhs = p.run(r);
            return new Tuple8<>(lhs.$1, lhs.$2, rhs.$1, rhs.$2, rhs.$3, rhs.$4, rhs.$5, rhs.$6);
        };
    }*/
    default Parser2<A,B> nextL(Parser<?> p) {
        return r -> {
            Tuple2<A,B> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}
