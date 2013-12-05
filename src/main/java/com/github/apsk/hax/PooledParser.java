package com.github.apsk.hax;

import javax.xml.stream.XMLStreamException;
import java.util.List;

public class PooledParser<R> implements Parser<R> {
    final Parser<R> parser;
    private PooledParser(Parser<R> parser) {
        this.parser = parser;
    }
    @Override
    public R run(HAXEventReader eventReader, R pool) throws XMLStreamException {
        return parser.run(eventReader, pool);
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
        return parser.purify();
    }
    public static <T> PooledParser<T> from(Parser<T> parser) {
        class Ref { public T val = null; }
        Ref selfPool = new Ref();
        Parser<T> pooledParser = (reader, pool) -> {
            if (pool != null) {
                return parser.run(reader, pool);
            }
            if (selfPool.val == null) {
                selfPool.val = parser.run(reader);
            } else {
                parser.run(reader, selfPool.val);
            }
            return selfPool.val;
        };
        return new PooledParser(pooledParser);
    }
}
