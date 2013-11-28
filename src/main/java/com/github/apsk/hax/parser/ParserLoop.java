package com.github.apsk.hax.parser;

import javax.xml.stream.XMLStreamException;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ParserLoop<T> implements Spliterator<T> {
    final Parser<T> parser;
    final Parser<Boolean> pred;
    final HAXEventReader reader;
    public ParserLoop(Parser<T> parser, Parser<Boolean> pred, HAXEventReader reader) {
        this.parser = parser;
        this.pred = pred;
        this.reader = reader;
    }
    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        try {
            if (pred.run(reader)) return false;
            action.accept(parser.run(reader));
            return true;
        } catch (XMLStreamException ignored) {
            return false;
        }
    }
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        try {
            for (;;) {
                if (pred.run(reader)) return;
                action.accept(parser.run(reader));
            }
        } catch (XMLStreamException ignored) {}
    }
    @Override
    public Spliterator<T> trySplit() {
        return null;
    }
    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }
    @Override
    public int characteristics() {
        return 0;
    }
}
