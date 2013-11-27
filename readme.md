```xml
<ops class="arith">
    <op name="sum"><lhs>3</lhs><rhs>6</rhs></op>
    <op name="mul"><lhs>3</lhs><rhs>3</rhs></op>
    <op name="sub"><lhs>10</lhs><rhs>1</rhs></op>
    <op name="div"><lhs>18</lhs><rhs>2</rhs></op>
</ops>
```

```java
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
```

```java
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
    Entry.class.getClassLoader().getResourceAsStream("ops.xml")
);
reader.skipTo("ops");
Tuple2<String,List<Op>> result =
    manyWithin("ops", attr("class"), op).run(reader);
```
