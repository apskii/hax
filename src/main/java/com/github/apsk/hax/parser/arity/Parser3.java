package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.*;

import javax.xml.stream.XMLStreamException;

import static com.github.apsk.j8t.Tuples.tuple;

@FunctionalInterface
public interface Parser3<A,B,C> extends Parser<Tuple3<A,B,C>> {
    /*default <D> Parser4<A,B,C,D> and(Parser<D> p) throws XMLStreamException {
        return r -> {
            Tuple3<A,B,C> lhs = this.run(r);
            D rhs = p.run(r);
            return tuple(lhs.$1, lhs.$2, lhs.$3, rhs);
        };
    }
    default <D,E> Parser5<A,B,C,D,E> and(Parser2<D,E> p) {
        return r -> {
            Tuple3<A,B,C> lhs = this.run(r);
            Tuple2<D,E> rhs = p.run(r);
            return new Tuple5<>(lhs.$1, lhs.$2, lhs.$3, rhs.$1, rhs.$2);
        };
    }
    default <D,E,F> Parser6<A,B,C,D,E,F> and(Parser3<D,E,F> p) {
        return r -> {
            Tuple3<A,B,C> lhs = this.run(r);
            Tuple3<D,E,F> rhs = p.run(r);
            return new Tuple6<>(lhs.$1, lhs.$2, lhs.$3, rhs.$1, rhs.$2, rhs.$3);
        };
    }
    default <D,E,F,G> Parser7<A,B,C,D,E,F,G> and(Parser4<D,E,F,G> p) {
        return r -> {
            Tuple3<A,B,C> lhs = this.run(r);
            Tuple4<D,E,F,G> rhs = p.run(r);
            return new Tuple7<>(lhs.$1, lhs.$2, lhs.$3, rhs.$1, rhs.$2, rhs.$3, rhs.$4);
        };
    }
    default <D,E,F,G,H> Parser8<A,B,C,D,E,F,G,H> and(Parser5<D,E,F,G,H> p) {
        return r -> {
            Tuple3<A,B,C> lhs = this.run(r);
            Tuple5<D,E,F,G,H> rhs = p.run(r);
            return new Tuple8<>(lhs.$1, lhs.$2, lhs.$3, rhs.$1, rhs.$2, rhs.$3, rhs.$4, rhs.$5);
        };
    }*/
    default Parser3<A,B,C> nextL(Parser<?> p) {
        return (reader, pool) -> {
            Tuple3<A,B,C> result = this.run(reader, pool);
            p.run(reader);
            return result;
        };
    }
}
