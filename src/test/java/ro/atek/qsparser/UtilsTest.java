package ro.atek.qsparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ro.atek.qsparser.value.*;

public class UtilsTest
{
   @Test
   void merge_with_null()
   {
      ArrayValue array = new ArrayValue(new Value[] { IntValue.get(42) });
      ArrayValue arrayWithNull = new ArrayValue(new Value[] { IntValue.get(42), NullValue.get() });
      ArrayValue nullWithArray = new ArrayValue(new Value[] { NullValue.get(), IntValue.get(42) });

      Assertions.assertEquals(array.merge(NullValue.get()), arrayWithNull);
      Assertions.assertEquals(NullValue.get().merge(array), nullWithArray);
   }

   @Test
   void merge()
   {
      DictValue dict1 = new DictValue().append(StringValue.get("a"), StringValue.get("b"));
      DictValue dict2 = new DictValue().append(StringValue.get("a"), StringValue.get("c"));

      Assertions.assertEquals(dict1.merge(dict2), new DictValue().append(StringValue.get("a"),
                      new ArrayValue(new Value[] { StringValue.get("b"), StringValue.get("c") })));
   }
}
