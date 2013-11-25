```xml
<ops>
    <op name="sum"><lhs>3</lhs><rhs>6</rhs></op>
    <op name="mul"><lhs>3</lhs><rhs>3</rhs></op>
    <op name="sub"><lhs>10</lhs><rhs>1</rhs></op>
    <op name="div"><lhs>18</lhs><rhs>2</rhs></op>
</ops>
```

```java
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
```
