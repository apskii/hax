package com.github.apsk.hax;

import org.junit.Test;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.util.List;
import java.util.function.Function;

import static com.github.apsk.hax.HAX.*;
import static junit.framework.Assert.assertEquals;

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
        Function<String,String> capitalize = s ->
            Character.toUpperCase(s.charAt(0)) + s.substring(1);
        Parser<Op> op = open("op")
            .nextR(attr("name"))
            .and(elemText("lhs"))
            .and(elemText("rhs"))
            .nextL(close("op"))
            .map(r -> new Op(
                Op.Type.valueOf(capitalize.apply(r.val1)),
                Integer.parseInt(r.val2),
                Integer.parseInt(r.val3)
            ));
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(
            getClass().getClassLoader().getResourceAsStream("ops.xml")
        );
        List<Op> ops = manyWithin("ops", op).run(reader);
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
    }
}
