package com.github.apsk.hax;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static com.github.apsk.hax.HAX.*;
import static com.github.apsk.hax.HAX.attr;
import static com.github.apsk.hax.HAX.manyWithin;

public class Entry {
    public static class Op {
        public enum Type { Sum, Sub, Mul, Div }
        public final Type type;
        public final int lhs, rhs;
        public Op(Type type, int lhs, int rhs) {
            this.type = type;
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }
    static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    public static void main(String[] args) throws XMLStreamException {
        Parser<Op> op =
            within("op", attr("name"),
                elemText("lhs"),
                elemText("rhs"))
                .map(r -> {
                    System.out.println(r.$1 + "|" + r.$2 + "|" + r.$3);
                    return new Op(
                        Op.Type.valueOf(capitalize(r.$1)),
                        Integer.parseInt(r.$2),
                        Integer.parseInt(r.$3)
                    );
                });
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(
            Entry.class.getClassLoader().getResourceAsStream("ops.xml")
        );
        skipTo("ops").run(reader);
        manyWithin("ops", attr("class"), op).run(reader);
    }
}
