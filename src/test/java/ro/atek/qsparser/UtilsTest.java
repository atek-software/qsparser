package ro.atek.qsparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ro.atek.qsparser.value.*;

class UtilsTest
{
   @Test
   void merge_with_null_value()
   {
      ArrayValue array = new ArrayValue(new Value[] { IntValue.get(42) });
      ArrayValue arrayWithNull = new ArrayValue(new Value[] { IntValue.get(42), NullValue.get() });
      ArrayValue nullWithArray = new ArrayValue(new Value[] { NullValue.get(), IntValue.get(42) });
      ArrayValue boolArray = new ArrayValue(new Value[] { NullValue.get(), BoolValue.TRUE });
      ArrayValue boolArray2 = new ArrayValue(new Value[] { BoolValue.TRUE, NullValue.get() });

      Assertions.assertEquals(array.merge(NullValue.get()), arrayWithNull);
      Assertions.assertEquals(NullValue.get().merge(array), nullWithArray);
      Assertions.assertEquals(NullValue.get().merge(BoolValue.TRUE), boolArray);
      Assertions.assertEquals(BoolValue.TRUE.merge(NullValue.get()), boolArray2);
   }

   @Test
   void merge()
   {
      DictValue dict1 = new DictValue().append(StringValue.get("a"), StringValue.get("b"));
      DictValue dict2 = new DictValue().append(StringValue.get("a"), StringValue.get("c"));
      DictValue dict3 = new DictValue().append(StringValue.get("a"), dict2);

      Assertions.assertEquals(dict1.merge(dict2), new DictValue().append(StringValue.get("a"),
                      new ArrayValue(new Value[] { StringValue.get("b"), StringValue.get("c") })));
      Assertions.assertEquals(dict1.merge(dict3), new DictValue().append(StringValue.get("a"),
                       new ArrayValue(new Value[] { StringValue.get("b"), new DictValue().append(
                          StringValue.get("a"), StringValue.get("c"))})));

      DictValue d1 = new DictValue().append(StringValue.get("foo"), new ArrayValue(new Value[] {
         StringValue.get("bar"), new DictValue().append(StringValue.get("first"), StringValue.get("123"))}));
      DictValue d2 = new DictValue().append(StringValue.get("foo"), new DictValue().append(
         StringValue.get("second"), StringValue.get("456")));

      Assertions.assertEquals(d1.merge(d2), new DictValue().append(StringValue.get("foo"),
         new DictValue().append(IntValue.get(0), StringValue.get("bar")).append(IntValue.get(1),
         new DictValue().append(StringValue.get("first"), StringValue.get("123")))
                                            .append(StringValue.get("second"), StringValue.get("456"))));

      DictValue a = new DictValue().append(StringValue.get("foo"), new ArrayValue(new Value[] {
         StringValue.get("baz") }));
      DictValue b = new DictValue().append(StringValue.get("foo"), new ArrayValue(new Value[] {
         StringValue.get("bar"), StringValue.get("xyzz") }));
      Assertions.assertEquals(a.merge(b), new DictValue().append(StringValue.get("foo"), new ArrayValue(
         new Value[] { StringValue.get("baz"), StringValue.get("bar"), StringValue.get("xyzz") })));

      DictValue x = new DictValue().append(StringValue.get("foo"), StringValue.get("baz"));
      Assertions.assertEquals(x.merge(StringValue.get("bar")), new DictValue().append(StringValue.get("foo"),
          StringValue.get("baz")).append(StringValue.get("bar"), BoolValue.TRUE));

    }
}
