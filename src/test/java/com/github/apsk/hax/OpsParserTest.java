package com.github.apsk.hax;

import javax.xml.stream.XMLStreamException;
import com.github.apsk.hax.parser.HAXEventReader;
import com.github.apsk.hax.parser.Parser;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.github.apsk.hax.HAX.*;

public class OpsParserTest {
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
    @Test
    public void checkOps() throws XMLStreamException {
        Parser<Op> op =
            within("op", attr("name"),
                elemText("lhs"),
                elemText("rhs"))
            .map(r -> new Op(
                Op.Type.valueOf(capitalize(r.$1)),
                Integer.parseInt(r.$2),
                Integer.parseInt(r.$3)
            ));
        HAXEventReader reader = new HAXEventReader(
            getClass().getClassLoader().getResourceAsStream("ops.xml")
        );
        reader.skipTo("ops");
        manyWithin("ops", attr("class"), op)
            .run(reader)
            .unpack((cls, ops) -> {
                assertEquals(cls, "arith");
                Op opA = ops.get(0);
                assertEquals(opA.type, Op.Type.Sum);
                assertEquals(opA.lhs, 3);
                assertEquals(opA.rhs, 6);
                Op opB = ops.get(1);
                assertEquals(opB.type, Op.Type.Mul);
                assertEquals(opB.lhs, 3);
                assertEquals(opB.rhs, 3);
                Op opC = ops.get(2);
                assertEquals(opC.type, Op.Type.Sub);
                assertEquals(opC.lhs, 10);
                assertEquals(opC.rhs, 1);
                Op opD = ops.get(3);
                assertEquals(opD.type, Op.Type.Div);
                assertEquals(opD.lhs, 18);
                assertEquals(opD.rhs, 2);
                return null;
            });
    }
}
