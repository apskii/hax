package com.github.apsk.hax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;

public final class SelfPooledParser<R> implements Parser<R> {
    final Parser<R> parser;
    R pool;
    private SelfPooledParser(Parser<R> parser) {
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
    public SelfPooledParser<R> nextL(Parser<?> otherParser) {
        return SelfPooledParser.from((reader, pool) -> {
            R result = this.run(reader, pool);
            otherParser.run(reader);
            return result;
        });
    }
    @Override
    public SelfPooledParser<List<R>> until(Parser<Boolean> predicateParser) {
        return parser.until(predicateParser);
    }
    @Override
    public Parser<R> purify() {
        return parser;
    }
    public static <T> SelfPooledParser<T> from(Parser<T> parser) {
        return new SelfPooledParser(parser.purify());
    }
}
