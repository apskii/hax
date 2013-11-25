package com.github.apsk.hax;

import com.github.apsk.hax.Parser;
import static junit.framework.Assert.*;
import org.junit.Test;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import java.util.List;

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
    @Test
    public void checkOps() throws XMLStreamException {
        Parser<Op> op = open("op")
            .nextR(attr("name"))
            .and(open("lhs").nextR(text()))
            .and(open("rhs").nextR(text()))
            .map(t -> {
                Op.Type type = null;
                switch (t.val1) {
                    case "sum": type = Op.Type.Sum; break;
                    case "sub": type = Op.Type.Sub; break;
                    case "mul": type = Op.Type.Mul; break;
                    case "div": type = Op.Type.Div; break;
                }
                return new Op(type,
                    Integer.parseInt(t.val2),
                    Integer.parseInt(t.val3)
                );
            });
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(
                getClass().getClassLoader().getResourceAsStream("ops.xml")
        );
        List<Op> ops = open("ops")
            .nextR(op.until(closing("ops")))
            .run(reader);
        Op opA = ops.get(0);
        assertEquals("ASDASD", opA.type, Op.Type.Sum);
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
    }
}
