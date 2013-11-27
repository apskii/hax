package com.github.apsk.hax.parser.arity;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.Tuple8;

@FunctionalInterface
public interface Parser8<A,B,C,D,E,F,G,H> extends Parser<Tuple8<A,B,C,D,E,F,G,H>> {
    default Parser8<A,B,C,D,E,F,G,H> nextL(Parser<?> p) {
        return r -> {
            Tuple8<A,B,C,D,E,F,G,H> result = this.run(r);
            p.run(r);
            return result;
        };
    }
}