package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.Tuple2;

@FunctionalInterface
public interface Parser1<A> extends Parser<A> {
    default <B> Parser2<A,B> and(Parser<B> p) {
        return r -> new Tuple2<>(this.run(r), p.run(r));
    }
    default Parser1<A> nextL(Parser<?> p) {
        return r -> {
            A result = this.run(r);
            p.run(r);
            return result;
        };
    }
}
