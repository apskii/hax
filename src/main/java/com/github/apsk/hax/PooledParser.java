package com.github.apsk.hax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;

public final class PooledParser<R> implements Parser<R> {
    final Parser<R> parser;
    R pool;
    private PooledParser(Parser<R> parser) {
        this.parser = parser;
    }
    @Override
    public R run(XMLStreamReader reader, R pool) throws XMLStreamException {
        if (pool != null) {
            return parser.run(reader, pool);
        }
        if (this.pool == null) {
            this.pool = parser.run(reader);
        } else {
            parser.run(reader, this.pool);
        }
        return this.pool;
    }
    @Override
    public PooledParser<R> nextL(Parser<?> p) {
        return PooledParser.from((reader, pool) -> {
            R result = this.run(reader, pool);
            p.run(reader);
            return result;
        });
    }
    @Override
    public PooledParser<List<R>> until(Parser<Boolean> pred) {
        return parser.until(pred);
    }
    @Override
    public Parser<R> purify() {
        return parser;
    }
    public static <T> PooledParser<T> from(Parser<T> parser) {
        return new PooledParser(parser.purify());
    }
}
