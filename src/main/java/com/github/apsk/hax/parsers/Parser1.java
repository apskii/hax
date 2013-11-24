package com.github.apsk.hax.parsers;

import com.github.apsk.hax.Parser;
import com.github.apsk.j8t.Tuple2;

@FunctionalInterface
public interface Parser1<A> extends Parser<A> {
    default <B> Parser2<A,B> and(Parser<B> p) {
        return r -> new Tuple2<>(this.run(r), p.run(r));
    }
    default <X> Parser1<A> nextL(Parser1<X> p) {
        return r -> {
            A result = this.run(r);
            p.run(r);
            return result;
        };
    }
    default <X> Parser1<X> nextR(Parser1<X> p) {
        return r -> {
            this.run(r);
            return p.run(r);
        };
    }
}
