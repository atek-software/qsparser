package ro.atek.qsparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ro.atek.qsparser.net.ContentType;
import ro.atek.qsparser.net.DefaultEncoder;
import ro.atek.qsparser.net.Encoder;
import ro.atek.qsparser.value.*;

class QueryStringBuilderTest
{
   private final StringValue a = StringValue.get("a");
   private final StringValue b = StringValue.get("b");
   private final StringValue c = StringValue.get("c");
   private final StringValue d = StringValue.get("d");
   private final StringValue e = StringValue.get("e");
   private final IntValue int1 = IntValue.get(1);
   private final IntValue int2 = IntValue.get(2);
   private final IntValue int3 = IntValue.get(3);

   @Test
   void stringifies_a_querystring_object()
   {
      QueryStringBuilder builder = new QueryStringBuilder();
      Assertions.assertEquals("a=b",
                              builder.stringify(new DictValue().append(a, b)));
      Assertions.assertEquals("a=1",
                              builder.stringify(new DictValue().append(a, int1)));
      Assertions.assertEquals("a=1&b=2",
                              builder.stringify(new DictValue().append(a, int1).append(b, int2)));
      Assertions.assertEquals("a=A_Z",
                              builder.stringify(new DictValue().append(a, StringValue.get("A_Z"))));
      Assertions.assertEquals("a=%E2%82%AC",
                              builder.stringify(new DictValue().append(a, StringValue.get("€"))));
      Assertions.assertEquals("a=%EE%80%80",
                              builder.stringify(new DictValue().append(a, StringValue.get("\uE000"))));
      Assertions.assertEquals("a=%D7%90",
                              builder.stringify(new DictValue().append(a, StringValue.get("א"))));
      Assertions.assertEquals("a=%F0%90%90%B7",
                              builder.stringify(new DictValue().append(a, StringValue.get("\uD801\uDC37"))));
   }

   @Test
   void stringifies_falsy_values()
   {
      QueryStringBuilder builder = new QueryStringBuilder();
      Assertions.assertEquals("", builder.stringify(null));
      Assertions.assertEquals("", builder.stringify(NullValue.get()));
      Assertions.assertEquals("", builder.stringify(BoolValue.FALSE));
      Assertions.assertEquals("", builder.stringify(IntValue.get(0)));
   }

   @Test
   void stringifies_ints()
   {
      Encoder encoder = (content, charset, type) ->
      {
         String value = DefaultEncoder.INSTANCE.encode(content, charset, type);
         if (type == ContentType.KEY)
         {
            return value;
         }
         try
         {
            Integer.parseInt(value);
            return value + "n";
         }
         catch (NumberFormatException e)
         {
            return value;
         }
      };
      QueryStringBuilder builder = new QueryStringBuilder();
      QueryStringBuilder builder2 = new QueryStringBuilder(new StringifyOptions().setEncoder(encoder));
      QueryStringBuilder builder3 = new QueryStringBuilder(new StringifyOptions()
                                                              .setEncodeValuesOnly(true)
                                                              .setArrayFormat(ArrayFormat.BRACKETS));
      QueryStringBuilder builder4 = new QueryStringBuilder(new StringifyOptions()
                                                              .setEncoder(encoder)
                                                              .setEncodeValuesOnly(true)
                                                              .setArrayFormat(ArrayFormat.BRACKETS));
      Value intArray = new ArrayValue(new Value[] { int3 });
      Assertions.assertEquals("", builder.stringify(int3));
      Assertions.assertEquals("0=3", builder.stringify(intArray));
      Assertions.assertEquals("0=3n", builder2.stringify(intArray));
      Assertions.assertEquals("a=3", builder.stringify(new DictValue().append(a, int3)));
      Assertions.assertEquals("a=3n", builder2.stringify(new DictValue().append(a, int3)));
      Assertions.assertEquals("a[]=3", builder3.stringify(new DictValue().append(a, intArray)));
      Assertions.assertEquals("a[]=3n", builder4.stringify(new DictValue().append(a, intArray)));
   }

   @Test
   void adds_query_prefix()
   {
      QueryStringBuilder builder = new QueryStringBuilder(new StringifyOptions().setAddQueryPrefix(true));
      Assertions.assertEquals("?a=b", builder.stringify(new DictValue().append(a, b)));
   }

   @Test
   void not_add_query_prefix()
   {
      QueryStringBuilder builder = new QueryStringBuilder(new StringifyOptions().setAddQueryPrefix(true));
      Assertions.assertEquals("", builder.stringify(new DictValue()));
   }

   @Test
   void stringifies_nested_falsy_values()
   {
      QueryStringBuilder builder1 = new QueryStringBuilder();
      QueryStringBuilder builder2 = new QueryStringBuilder(
         new StringifyOptions().setStrictNullHandling(true));
      Assertions.assertEquals("a%5Bb%5D%5Bc%5D=",
                              builder1.stringify(new DictValue().append(a,
                                                 new DictValue().append(b,
                                                 new DictValue().append(c, NullValue.get())))));
      Assertions.assertEquals("a%5Bb%5D%5Bc%5D",
                              builder2.stringify(new DictValue().append(a,
                                                 new DictValue().append(b,
                                                 new DictValue().append(c, NullValue.get())))));
      Assertions.assertEquals("a%5Bb%5D%5Bc%5D=false",
                              builder1.stringify(new DictValue().append(a,
                                                 new DictValue().append(b,
                                                 new DictValue().append(c, BoolValue.FALSE)))));
   }

   @Test
   void stringifies_a_nested_object()
   {
      QueryStringBuilder builder = new QueryStringBuilder();

      Assertions.assertEquals("a%5Bb%5D=c",
                              builder.stringify(new DictValue().append(a, new DictValue().append(b, c))));
      Assertions.assertEquals("a%5Bb%5D%5Bc%5D%5Bd%5D=e",
                              builder.stringify(new DictValue().append(a,
                                                new DictValue().append(b,
                                                new DictValue().append(c,
                                                new DictValue().append(d, e))))));
   }

   @Test
   void stringifies_a_nested_object_with_dots_notation()
   {
      QueryStringBuilder builder = new QueryStringBuilder(new StringifyOptions().setAllowDots(true));

      Assertions.assertEquals("a.b=c",
                              builder.stringify(new DictValue().append(a, new DictValue().append(b, c))));
      Assertions.assertEquals("a.b.c.d=e",
                              builder.stringify(new DictValue().append(a,
                                                new DictValue().append(b,
                                                new DictValue().append(c,
                                                new DictValue().append(d, e))))));

   }

   @Test
   void stringifies_an_array_value()
   {
      QueryStringBuilder builder = new QueryStringBuilder(new StringifyOptions()
                                                             .setArrayFormat(ArrayFormat.INDICES));
      QueryStringBuilder builder2 = new QueryStringBuilder(new StringifyOptions()
                                                             .setArrayFormat(ArrayFormat.BRACKETS));
      QueryStringBuilder builder3 = new QueryStringBuilder(new StringifyOptions()
                                                              .setArrayFormat(ArrayFormat.COMMA));
      QueryStringBuilder builder4 = new QueryStringBuilder();
      Assertions.assertEquals("a%5B0%5D=b&a%5B1%5D=c&a%5B2%5D=d",
                              builder.stringify(new DictValue().append(a, new ArrayValue(
                                 new Value[] { b, c, d }))));
      Assertions.assertEquals("a%5B%5D=b&a%5B%5D=c&a%5B%5D=d",
                              builder2.stringify(new DictValue().append(a, new ArrayValue(
                                 new Value[] { b, c, d }))));
      Assertions.assertEquals("a=b%2Cc%2Cd",
                              builder3.stringify(new DictValue().append(a, new ArrayValue(
                                 new Value[] { b, c, d }))));
      Assertions.assertEquals("a%5B0%5D=b&a%5B1%5D=c&a%5B2%5D=d",
                              builder4.stringify(new DictValue().append(a, new ArrayValue(
                                 new Value[] { b, c, d }))));
   }

   @Test
   void omits_nulls_when_asked()
   {
      QueryStringBuilder builder = new QueryStringBuilder(new StringifyOptions().setSkipsNulls(true));
      Assertions.assertEquals("a=b",
                              builder.stringify(new DictValue().append(a, b).append(c, NullValue.get())));
   }

   @Test
   void omits_nested_nulls_when_asked()
   {
      QueryStringBuilder builder = new QueryStringBuilder(new StringifyOptions().setSkipsNulls(true));
      Assertions.assertEquals("a%5Bb%5D=c",
                              builder.stringify(new DictValue().append(a,
                                                new DictValue().append(b, c).append(d, NullValue.get()))));
   }

   @Test
   void omits_array_indices_when_asked()
   {
      QueryStringBuilder builder = new QueryStringBuilder(new StringifyOptions().setIndices(false));
      Assertions.assertEquals("a=b&a=c&a=d", builder.stringify(
         new DictValue().append(a, new ArrayValue(new Value[] { b, c, d }))));
   }

   @Test
   void non_array_item()
   {
      StringifyOptions options = new StringifyOptions().setEncodeValuesOnly(true);
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, c);

      Assertions.assertEquals("a=c", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a=c", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a=c", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=c", builder.stringify(value));
   }

   @Test
   void array_with_single_item()
   {
      StringifyOptions options = new StringifyOptions().setEncodeValuesOnly(true);
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, new ArrayValue(new Value[] { c }));

      Assertions.assertEquals("a[0]=c", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a[0]=c", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a[]=c", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=c", builder.stringify(value));
      options.setCommaRoundTrip(true);
      Assertions.assertEquals("a[]=c", builder.stringify(value));
   }

   @Test
   void array_with_multiple_items()
   {
      StringifyOptions options = new StringifyOptions().setEncodeValuesOnly(true);
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, new ArrayValue(new Value[] { c, d }));

      Assertions.assertEquals("a[0]=c&a[1]=d", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a[0]=c&a[1]=d", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a[]=c&a[]=d", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=c,d", builder.stringify(value));
   }

   @Test
   void array_with_multiple_items_with_a_comma()
   {
      StringifyOptions options = new StringifyOptions();
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, new ArrayValue(new Value[] { StringValue.get("c,d"), e }));

      options.setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=c%2Cd%2Ce", builder.stringify(value));
      options.setEncodeValuesOnly(true);
      Assertions.assertEquals("a=c%2Cd,e", builder.stringify(value));
   }

   @Test
   void stringifies_a_nested_array_value()
   {
      StringifyOptions options = new StringifyOptions().setEncodeValuesOnly(true);
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, new DictValue().append(b,
                                                                     new ArrayValue(new Value[] { c, d })));

      Assertions.assertEquals("a[b][0]=c&a[b][1]=d", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a[b][0]=c&a[b][1]=d", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a[b][]=c&a[b][]=d", builder.stringify(value));
      options.setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a[b]=c,d", builder.stringify(value));
   }

   @Test
   void stringifies_comma_and_empty_array_values()
   {
      StringifyOptions options = new StringifyOptions();
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, new ArrayValue(new Value[] { StringValue.get(","),
         StringValue.EMPTY,
         StringValue.get("c,d%") }));

      options.setEncode(false).setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a[0]=,&a[1]=&a[2]=c,d%", builder.stringify(value));
      options.setEncode(false).setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a[]=,&a[]=&a[]=c,d%", builder.stringify(value));
      options.setEncode(false).setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=,,,c,d%", builder.stringify(value));
      options.setEncode(false).setArrayFormat(ArrayFormat.REPEAT);
      Assertions.assertEquals("a=,&a=&a=c,d%", builder.stringify(value));

      options.setEncode(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a[0]=%2C&a[1]=&a[2]=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a[]=%2C&a[]=&a[]=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=%2C,,c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.REPEAT);
      Assertions.assertEquals("a=%2C&a=&a=c%2Cd%25", builder.stringify(value));

      options.setEncode(true).setEncodeValuesOnly(false).setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a%5B0%5D=%2C&a%5B1%5D=&a%5B2%5D=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(false).setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a%5B%5D=%2C&a%5B%5D=&a%5B%5D=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(false).setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=%2C%2C%2Cc%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(false).setArrayFormat(ArrayFormat.REPEAT);
      Assertions.assertEquals("a=%2C&a=&a=c%2Cd%25", builder.stringify(value));
   }

   @Test
   void stringifies_comma_and_empty_non_array_values()
   {
      StringifyOptions options = new StringifyOptions();
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, StringValue.get(","))
                                   .append(b, StringValue.get(""))
                                   .append(c, StringValue.get("c,d%"));

      options.setEncode(false).setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a=,&b=&c=c,d%", builder.stringify(value));
      options.setEncode(false).setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a=,&b=&c=c,d%", builder.stringify(value));
      options.setEncode(false).setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=,&b=&c=c,d%", builder.stringify(value));
      options.setEncode(false).setArrayFormat(ArrayFormat.REPEAT);
      Assertions.assertEquals("a=,&b=&c=c,d%", builder.stringify(value));

      options.setEncode(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a=%2C&b=&c=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a=%2C&b=&c=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=%2C&b=&c=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.REPEAT);
      Assertions.assertEquals("a=%2C&b=&c=c%2Cd%25", builder.stringify(value));

      options.setEncode(true).setEncodeValuesOnly(false).setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a=%2C&b=&c=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(false).setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a=%2C&b=&c=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(false).setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=%2C&b=&c=c%2Cd%25", builder.stringify(value));
      options.setEncode(true).setEncodeValuesOnly(false).setArrayFormat(ArrayFormat.REPEAT);
      Assertions.assertEquals("a=%2C&b=&c=c%2Cd%25", builder.stringify(value));
   }

   @Test
   void stringifies_a_nested_array_value_with_dots_notation()
   {
      StringifyOptions options = new StringifyOptions();
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, new DictValue().append(b,
                                                                     new ArrayValue(new Value[]{ c, d })));

      options.setAllowDots(true).setEncodeValuesOnly(true);
      Assertions.assertEquals("a.b[0]=c&a.b[1]=d", builder.stringify(value));
      options.setAllowDots(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a.b[0]=c&a.b[1]=d", builder.stringify(value));
      options.setAllowDots(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a.b[]=c&a.b[]=d", builder.stringify(value));
      options.setAllowDots(true).setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a.b=c,d", builder.stringify(value));
   }

   @Test
   void stringifies_an_object_inside_an_array()
   {
      StringifyOptions options = new StringifyOptions();
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a, new ArrayValue(new Value[]{ new DictValue().append(b, c) }));
      Value value2 = new DictValue().append(a, new ArrayValue(new Value[]{ new DictValue().append(b,
                     new DictValue().append(c, new ArrayValue(new Value[] { int1 }))) }));

      Assertions.assertEquals("a%5B0%5D%5Bb%5D=c", builder.stringify(value));
      Assertions.assertEquals("a%5B0%5D%5Bb%5D%5Bc%5D%5B0%5D=1", builder.stringify(value2));
      options.setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a%5B0%5D%5Bb%5D=c", builder.stringify(value));
      Assertions.assertEquals("a%5B0%5D%5Bb%5D%5Bc%5D%5B0%5D=1", builder.stringify(value2));
      options.setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a%5B%5D%5Bb%5D=c", builder.stringify(value));
      Assertions.assertEquals("a%5B%5D%5Bb%5D%5Bc%5D%5B%5D=1", builder.stringify(value2));
   }

   @Test
   void stringifies_an_array_with_mixed_objects_and_primitives()
   {
      StringifyOptions options = new StringifyOptions();
      QueryStringBuilder builder = new QueryStringBuilder(options);
      Value value = new DictValue().append(a,
                    new ArrayValue(new Value[]{ new DictValue().append(b, int1), int2, int3}));
      options.setEncodeValuesOnly(true);
      Assertions.assertEquals("a[0][b]=1&a[1]=2&a[2]=3", builder.stringify(value));
      options.setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.INDICES);
      Assertions.assertEquals("a[0][b]=1&a[1]=2&a[2]=3", builder.stringify(value));
      options.setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.BRACKETS);
      Assertions.assertEquals("a[][b]=1&a[]=2&a[]=3", builder.stringify(value));
      options.setEncodeValuesOnly(true).setArrayFormat(ArrayFormat.COMMA);
      Assertions.assertEquals("a=DictValue,2,3", builder.stringify(value));
   }

   @Test
   void poc()
   {
      QueryStringBuilder builder = new QueryStringBuilder();
      StringValue address = StringValue.get("address");
      StringValue city = StringValue.get("city");
      StringValue ny = StringValue.get("New York");
      String queryString = builder.stringify(new DictValue().append(address,
                                                                    new DictValue().append(city, ny)));
      Assertions.assertEquals("address%5Bcity%5D=New+York", queryString);
   }
}
