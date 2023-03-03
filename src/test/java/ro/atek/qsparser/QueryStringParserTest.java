package ro.atek.qsparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ro.atek.qsparser.value.ArrayValue;
import ro.atek.qsparser.value.DictValue;
import ro.atek.qsparser.value.IntValue;
import ro.atek.qsparser.value.StringValue;
import ro.atek.qsparser.value.NullValue;
import ro.atek.qsparser.value.Value;

class QueryStringParserTest
{
   @Test
   public void parse_a_simple_string()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserSNH = new QueryStringParser(new ParserOptions().strictNullHandling(true));

      Assertions.assertEquals(parser.parse("0=foo"),
                              new DictValue().append(IntValue.get(0), StringValue.get("foo")));
      Assertions.assertEquals(parser.parse("foo=c++"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("c  ")));
      Assertions.assertEquals(parser.parse("a[>=]=23"),
                              new DictValue().append(new StringValue("a"),
                                                     new DictValue().append(StringValue.get(">="),
                                                                            StringValue.get("23"))));
      Assertions.assertEquals(parser.parse("a[<=>]==23"),
                              new DictValue().append(new StringValue("a"),
                                                     new DictValue().append(StringValue.get("<=>"),
                                                                            StringValue.get("=23"))));
      Assertions.assertEquals(parser.parse("a[==]=23"),
                              new DictValue().append(new StringValue("a"),
                                                     new DictValue().append(StringValue.get("=="),
                                                                            StringValue.get("23"))));
      Assertions.assertEquals(parserSNH.parse("foo"),
                              new DictValue().append(StringValue.get("foo"), NullValue.get()));
      Assertions.assertEquals(parser.parse("foo"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("")));
      Assertions.assertEquals(parser.parse("foo="),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("")));
      Assertions.assertEquals(parser.parse("foo=bar"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("bar")));
      Assertions.assertEquals(parser.parse(" foo = bar = baz "),
                              new DictValue().append(StringValue.get(" foo "), StringValue.get(" bar = baz ")));
      Assertions.assertEquals(parser.parse("foo=bar=baz"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("bar=baz")));
      Assertions.assertEquals(parser.parse("foo=bar&bar=baz"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("bar"))
                                             .append(StringValue.get("bar"), StringValue.get("baz")));
      Assertions.assertEquals(parser.parse("foo2=bar2&baz2="),
                              new DictValue().append(StringValue.get("foo2"), StringValue.get("bar2"))
                                             .append(StringValue.get("baz2"), StringValue.get("")));
      Assertions.assertEquals(parserSNH.parse("foo=bar&baz"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("bar"))
                                             .append(StringValue.get("baz"), NullValue.get()));
      Assertions.assertEquals(parser.parse("foo=bar&baz"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("bar"))
                                             .append(StringValue.get("baz"), StringValue.get("")));
      Assertions.assertEquals(parser.parse("cht=p3&chd=t:60,40&chs=250x100&chl=Hello|World"),
                              new DictValue().append(StringValue.get("cht"), StringValue.get("p3"))
                                             .append(StringValue.get("chd"), StringValue.get("t:60,40"))
                                             .append(StringValue.get("chs"), StringValue.get("250x100"))
                                             .append(StringValue.get("chl"), StringValue.get("Hello|World")));
   }

   @Test
   public void arrays_on_the_same_key()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("a[]=b&a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] {
                                                        StringValue.get("b"),
                                                        StringValue.get("c")
                                                     })));
      Assertions.assertEquals(parser.parse("a[0]=b&a[1]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] {
                                                        StringValue.get("b"),
                                                        StringValue.get("c")
                                                     })));
      Assertions.assertEquals(parser.parse("a=b,c"),
                              new DictValue().append(StringValue.get("a"), StringValue.get("b,c")));
      Assertions.assertEquals(parser.parse("a=b&a=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[]{
                                                        StringValue.get("b"),
                                                        StringValue.get("c")
                                                     })));
   }

   @Test
   public void allow_dot_notation()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserAD = new QueryStringParser(new ParserOptions().allowDots(true));
      Assertions.assertEquals(parser.parse("a.b=c"),
                              new DictValue().append(StringValue.get("a.b"), StringValue.get("c")));
      Assertions.assertEquals(parserAD.parse("a.b=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(StringValue.get("b"),
                                                                            StringValue.get("c"))));
   }

   @Test
   public void depth_parsing()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserD1 = new QueryStringParser(new ParserOptions().depth(1));
      QueryStringParser parserD0 = new QueryStringParser(new ParserOptions().depth(0));
      Assertions.assertEquals(parser.parse("a[b]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(StringValue.get("b"),
                                                                            StringValue.get("c"))));
      Assertions.assertEquals(parser.parse("a[b][c]=d"),
                              new DictValue().append(StringValue.get("a"),
                                 new DictValue().append(StringValue.get("b"),
                                    new DictValue().append(StringValue.get("c"), StringValue.get("d")))));
      Assertions.assertEquals(parser.parse("a[b][c][d][e][f][g][h]=i"),
                              new DictValue().append(StringValue.get("a"),
                                 new DictValue().append(StringValue.get("b"),
                                    new DictValue().append(StringValue.get("c"),
                                       new DictValue().append(StringValue.get("d"),
                                          new DictValue().append(StringValue.get("e"),
                                              new DictValue().append(StringValue.get("f"),
                                                 new DictValue().append(StringValue.get("[g][h]"),
                                                                        StringValue.get("i")))))))));
      Assertions.assertEquals(parserD1.parse("a[b][c]=d"),
                              new DictValue().append(StringValue.get("a"),
                                 new DictValue().append(StringValue.get("b"),
                                    new DictValue().append(StringValue.get("[c]"),
                                                           StringValue.get("d")))));
      Assertions.assertEquals(parserD1.parse("a[b][c][d]=e"),
                              new DictValue().append(StringValue.get("a"),
                                 new DictValue().append(StringValue.get("b"),
                                    new DictValue().append(StringValue.get("[c][d]"),
                                                           StringValue.get("e")))));
      Assertions.assertEquals(parserD0.parse("a[0]=b&a[1]=c"),
                              new DictValue().append(StringValue.get("a[0]"), StringValue.get("b"))
                                             .append(StringValue.get("a[1]"), StringValue.get("c")));
      Assertions.assertEquals(parserD0.parse("a[0][0]=b&a[0][1]=c&a[1]=d&e=2"),
                              new DictValue().append(StringValue.get("a[0][0]"), StringValue.get("b"))
                                             .append(StringValue.get("a[0][1]"), StringValue.get("c"))
                                             .append(StringValue.get("a[1]"), StringValue.get("d"))
                                             .append(StringValue.get("e"), StringValue.get("2")));
   }

   @Test
   public void parses_an_explicit_array()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("a[]=b"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b") })));
      Assertions.assertEquals(parser.parse("a[]=b&a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                                                  StringValue.get("c")})));
      Assertions.assertEquals(parser.parse("a[]=b&a[]=c&a[]=d"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c"), StringValue.get("d")})));
   }

   @Test
   public void parses_a_mix_of_simple_and_explicit_arrays()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parser20 = new QueryStringParser(new ParserOptions().arrayLimit(20));
      QueryStringParser parser0 = new QueryStringParser(new ParserOptions().arrayLimit(0));
      Assertions.assertEquals(parser.parse("a=b&a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                                                  StringValue.get("c")})));
      Assertions.assertEquals(parser.parse("a[]=b&a=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                                                  StringValue.get("c")})));
      Assertions.assertEquals(parser.parse("a[0]=b&a=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
      Assertions.assertEquals(parser.parse("a=b&a[0]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
      Assertions.assertEquals(parser20.parse("a[1]=b&a=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
      Assertions.assertEquals(parser0.parse("a[]=b&a=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
      Assertions.assertEquals(parser.parse("a[]=b&a=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
      Assertions.assertEquals(parser20.parse("a=b&a[1]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
      Assertions.assertEquals(parser0.parse("a=b&a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
      Assertions.assertEquals(parser.parse("a=b&a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
   }

   @Test
   public void parses_a_nested_array()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("a[b][]=c&a[b][]=d"),
                              new DictValue().append(StringValue.get("a"),
                                  new DictValue().append(StringValue.get("b"),
                                                         new ArrayValue(new Value[] { StringValue.get("c"),
                                                            StringValue.get("d")}))));
      Assertions.assertEquals(parser.parse("a[>=]=25"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(StringValue.get(">="),
                                                                            StringValue.get("25"))));
   }

   @Test
   public void allows_to_specify_array_indices()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parser20 = new QueryStringParser(new ParserOptions().arrayLimit(20));
      QueryStringParser parser0 = new QueryStringParser(new ParserOptions().arrayLimit(0));
      Assertions.assertEquals(parser.parse("a[1]=c&a[0]=b&a[2]=d"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                                                  StringValue.get("c"),
                                                                                  StringValue.get("d")})));
      Assertions.assertEquals(parser.parse("a[1]=c&a[0]=b"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("b"),
                                                        StringValue.get("c")})));
      Assertions.assertEquals(parser20.parse("a[1]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("c") })));
      Assertions.assertEquals(parser0.parse("a[1]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(1),
                                                                            StringValue.get("c"))));
      Assertions.assertEquals(parser.parse("a[1]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("c") })));
   }

   @Test
   public void limits_specific_array_indices_to_arrayLimit()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parser20 = new QueryStringParser(new ParserOptions().arrayLimit(20));
      Assertions.assertEquals(parser20.parse("a[20]=a"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("a") })));
      Assertions.assertEquals(parser20.parse("a[21]=a"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(21),
                                                                            StringValue.get("a"))));
      Assertions.assertEquals(parser.parse("a[20]=a"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("a") })));
      Assertions.assertEquals(parser.parse("a[21]=a"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(21),
                                                                            StringValue.get("a"))));
   }

   @Test
   public void supports_keys_that_begin_with_a_number()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("a[12b]=c"),
                              new DictValue().append(StringValue.get("a"),
                                 new DictValue().append(StringValue.get("12b"), StringValue.get("c"))));
   }

   @Test
   public void supports_encoded_equal_signs()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("he%3Dllo=th%3Dere"),
                              new DictValue().append(StringValue.get("he=llo"),
                                                     StringValue.get("th=ere")));
   }

   @Test
   public void is_ok_with_url_encoded_strings()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("a[b%20c]=d"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(StringValue.get("b c"),
                                                                            StringValue.get("d"))));
      Assertions.assertEquals(parser.parse("a[b]=c%20d"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(StringValue.get("b"),
                                                                            StringValue.get("c d"))));
   }

   @Test
   public void allows_brackets_in_the_value()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("pets=[\"tobi\"]"),
                              new DictValue().append(StringValue.get("pets"),
                                                     StringValue.get("[\"tobi\"]")));
      Assertions.assertEquals(parser.parse("operators=[\">=\", \"<=\"]"),
                              new DictValue().append(StringValue.get("operators"),
                                                     StringValue.get("[\">=\", \"<=\"]")));
   }

   @Test
   public void allows_empty_values()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse(""), new DictValue());
      Assertions.assertEquals(parser.parse(null), new DictValue());
   }

   @Test
   public void transforms_arrays_to_objects()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("foo[0]=bar&foo[bad]=baz"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(IntValue.get(0),
                                                                            StringValue.get("bar"))
                                                                    .append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))));
      Assertions.assertEquals(parser.parse("foo[bad]=baz&foo[0]=bar"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))
                                                                    .append(IntValue.get(0),
                                                                            StringValue.get("bar"))));
      Assertions.assertEquals(parser.parse("foo[bad]=baz&foo[]=bar"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))
                                                                    .append(IntValue.get(0),
                                                                            StringValue.get("bar"))));
      Assertions.assertEquals(parser.parse("foo[]=bar&foo[bad]=baz"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(IntValue.get(0),
                                                                            StringValue.get("bar"))
                                                                    .append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))));
      Assertions.assertEquals(parser.parse("foo[bad]=baz&foo[]=bar&foo[]=foo"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))
                                                                    .append(IntValue.get(0),
                                                                            StringValue.get("bar"))
                                                                    .append(IntValue.get(1),
                                                                            StringValue.get("foo"))));
      Assertions.assertEquals(parser.parse("foo[0][a]=a&foo[0][b]=b&foo[1][a]=aa&foo[1][b]=bb"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new ArrayValue(new Value[] {
                                                        new DictValue().append(StringValue.get("a"),
                                                                               StringValue.get("a"))
                                                                       .append(StringValue.get("b"),
                                                                               StringValue.get("b")),
                                                        new DictValue().append(StringValue.get("a"),
                                                                               StringValue.get("aa"))
                                                                       .append(StringValue.get("b"),
                                                                               StringValue.get("bb"))
                                                     })));
   }

   @Test
   public void transforms_arrays_to_objects_dot_notation()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().allowDots(true));
      Assertions.assertEquals(parser.parse("foo[0].baz=bar&fool.bad=baz"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new ArrayValue(new Value[] {
                                                        new DictValue().append(StringValue.get("baz"),
                                                                               StringValue.get("bar"))}))
                                             .append(StringValue.get("fool"),
                                                        new DictValue().append(StringValue.get("bad"),
                                                                               StringValue.get("baz"))));
      Assertions.assertEquals(parser.parse("foo[0].baz=bar&fool.bad.boo=baz"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new ArrayValue(new Value[] {
                                                        new DictValue().append(StringValue.get("baz"),
                                                                               StringValue.get("bar"))}))
                                             .append(StringValue.get("fool"),
                                                     new DictValue().append(StringValue.get("bad"),
                                                                            new DictValue().append(
                                                                               StringValue.get("boo"),
                                                                               StringValue.get("baz")))));
      Assertions.assertEquals(parser.parse("foo[0][0].baz=bar&fool.bad=baz"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new ArrayValue(new Value[] {
                                                        new ArrayValue(new Value[] {
                                                          new DictValue().append(StringValue.get("baz"),
                                                                                 StringValue.get("bar"))})}))
                                             .append(StringValue.get("fool"),
                                                     new DictValue().append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))));
      Assertions.assertEquals(parser.parse("foo[0].baz[0]=15&foo[0].bar=2"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new ArrayValue(new Value[]{
                                                        new DictValue().append(StringValue.get("baz"),
                                                                               new ArrayValue(new Value[] {
                                                                                  StringValue.get("15")}))
                                             .append(StringValue.get("bar"), StringValue.get("2"))})));
      Assertions.assertEquals(parser.parse("foo[0].baz[0]=15&foo[0].baz[1]=16&foo[0].bar=2"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new ArrayValue(new Value[]{
                                                        new DictValue().append(StringValue.get("baz"),
                                                                               new ArrayValue(new Value[] {
                                                                                  StringValue.get("15"),
                                                                                  StringValue.get("16")}))
                                                           .append(StringValue.get("bar"),
                                                                   StringValue.get("2"))})));
      Assertions.assertEquals(parser.parse("foo.bad=baz&foo[0]=bar"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))
                                                                    .append(IntValue.get(0),
                                                                            StringValue.get("bar"))));
      Assertions.assertEquals(parser.parse("foo.bad=baz&foo[]=bar"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))
                                                                    .append(IntValue.get(0),
                                                                            StringValue.get("bar"))));
      Assertions.assertEquals(parser.parse("foo[]=bar&foo.bad=baz"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(IntValue.get(0),
                                                                            StringValue.get("bar"))
                                                                    .append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))));
      Assertions.assertEquals(parser.parse("foo.bad=baz&foo[]=bar&foo[]=foo"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new DictValue().append(StringValue.get("bad"),
                                                                            StringValue.get("baz"))
                                                                    .append(IntValue.get(0),
                                                                            StringValue.get("bar"))
                                                                    .append(IntValue.get(1),
                                                                            StringValue.get("foo"))));
      Assertions.assertEquals(parser.parse("foo[0].a=a&foo[0].b=b&foo[1].a=aa&foo[1].b=bb"),
                              new DictValue().append(StringValue.get("foo"),
                                                     new ArrayValue(new Value[] {
                                                        new DictValue().append(StringValue.get("a"),
                                                                               StringValue.get("a"))
                                                           .append(StringValue.get("b"),
                                                                   StringValue.get("b")),
                                                        new DictValue().append(StringValue.get("a"),
                                                                               StringValue.get("aa"))
                                                           .append(StringValue.get("b"),
                                                                   StringValue.get("bb"))
                                                     })));
   }

   @Test
   public void correctly_prunes_undefined_values()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("a[2]=b&a[99999999]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(2),
                                                                            StringValue.get("b"))
                                                                    .append(IntValue.get(99999999),
                                                                            StringValue.get("c"))));
   }

   @Test
   @Disabled
   public void supports_malformed_uri_characters()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserSNH = new QueryStringParser(new ParserOptions().strictNullHandling(true));
      Assertions.assertEquals(parserSNH.parse("{%:%}"),
                              new DictValue().append(StringValue.get("{%:%}"),
                                                     NullValue.get()));
      Assertions.assertEquals(parser.parse("{%:%}="),
                              new DictValue().append(StringValue.get("{%:%}"),
                                                     StringValue.get("")));
      Assertions.assertEquals(parser.parse("foo=%:%}"),
                              new DictValue().append(StringValue.get("foo"),
                                                     StringValue.get("%:%}")));
   }

   @Test
   public void does_not_produce_empty_keys()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("_r=1&"),
                              new DictValue().append(StringValue.get("_r"),
                                                     StringValue.get("1")));
   }

   @Test
   public void parses_arrays_of_objects()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("a[][b]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[]{
                                                        new DictValue().append(StringValue.get("b"),
                                                                               StringValue.get("c"))})));
      Assertions.assertEquals(parser.parse("a[0][b]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[]{
                                                        new DictValue().append(StringValue.get("b"),
                                                                               StringValue.get("c"))})));
   }

   @Test
   public void allows_for_empty_strings_in_arrays()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserSNH20 = new QueryStringParser(new ParserOptions().strictNullHandling(true)
                                                                               .arrayLimit(20));
      QueryStringParser parserSNH0 = new QueryStringParser(new ParserOptions().strictNullHandling(true)
                                                                               .arrayLimit(0));
      Assertions.assertEquals(parser.parse("a[]=b&a[]=&a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[]{ StringValue.get("b"),
                                                        StringValue.get(""), StringValue.get("c")})));
      Assertions.assertEquals(parserSNH20.parse("a[0]=b&a[1]&a[2]=c&a[19]="),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] {
                                                        StringValue.get("b"), NullValue.get(),
                                                        StringValue.get("c"), StringValue.get("") })));
      Assertions.assertEquals(parserSNH0.parse("a[]=b&a[]&a[]=c&a[]="),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] {
                                                        StringValue.get("b"), NullValue.get(),
                                                        StringValue.get("c"), StringValue.get("") })));
      Assertions.assertEquals(parserSNH20.parse("a[0]=b&a[1]=&a[2]=c&a[19]"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] {
                                                        StringValue.get("b"), StringValue.get(""),
                                                        StringValue.get("c"), NullValue.get()})));
      Assertions.assertEquals(parserSNH0.parse("a[]=b&a[]=&a[]=c&a[]"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] {
                                                        StringValue.get("b"), StringValue.get(""),
                                                        StringValue.get("c"), NullValue.get()})));
      Assertions.assertEquals(parserSNH0.parse("a[]=&a[]=b&a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] {
                                                        StringValue.get(""), StringValue.get("b"),
                                                        StringValue.get("c") })));
   }

   @Test
   public void compacts_sparse_arrays()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().arrayLimit(20));
      Assertions.assertEquals(parser.parse("a[10]=1&a[2]=2"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[]{ StringValue.get("2"),
                                                        StringValue.get("1")})));
      Assertions.assertEquals(parser.parse("a[1][b][2][c]=1"),
                              new DictValue().append(StringValue.get("a"),
                                   new ArrayValue(new Value[]{
                                      new DictValue().append(StringValue.get("b"),
                                           new ArrayValue(new Value[]{
                                              new DictValue().append(StringValue.get("c"),
                                                                     StringValue.get("1"))}))})));
      Assertions.assertEquals(parser.parse("a[1][2][3][c]=1"),
                              new DictValue().append(StringValue.get("a"),
                                  new ArrayValue(new Value[]{
                                     new ArrayValue(new Value[]{
                                        new ArrayValue(new Value[]{
                                           new DictValue().append(StringValue.get("c"),
                                                            StringValue.get("1"))})})})));
      Assertions.assertEquals(parser.parse("a[1][2][3][c][1]=1"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[]{
                                                        new ArrayValue(new Value[]{
                                                           new ArrayValue(new Value[]{
                                                              new DictValue().append(StringValue.get("c"),
                                                                new ArrayValue(new Value[]{
                                                                   StringValue.get("1") }))})})})));
   }

   @Test
   public void parses_sparse_arrays()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().allowSparse(true));
      Assertions.assertEquals(parser.parse("a[4]=1&a[1]=2"),
                              new DictValue().append(StringValue.get("a"),
                                      new ArrayValue(new Value[]{
                                         null, StringValue.get("2"), null, null, StringValue.get("1")})));
      Assertions.assertEquals(parser.parse("a[1][b][2][c]=1"),
                              new DictValue().append(StringValue.get("a"),
                                   new ArrayValue(new Value[]{
                                      null, new DictValue().append(StringValue.get("b"),
                                            new ArrayValue(new Value[]{ null, null,
                                                new DictValue().append(StringValue.get("c"),
                                                                       StringValue.get("1"))}))})));
      Assertions.assertEquals(parser.parse("a[1][2][3][c]=1"),
                              new DictValue().append(StringValue.get("a"),
                                  new ArrayValue(new Value[]{
                                       null, new ArrayValue(new Value[]{
                                          null, null, new ArrayValue(new Value[]{
                                             null, null, null, new DictValue().append(StringValue.get("c"),
                                                   StringValue.get("1"))})})})));
      Assertions.assertEquals(parser.parse("a[1][2][3][c][1]=1"),
                              new DictValue().append(StringValue.get("a"),
                                   new ArrayValue(new Value[]{
                                      null, new ArrayValue(new Value[]{
                                      null, null, new ArrayValue(new Value[]{
                                      null, null, null, new DictValue().append(StringValue.get("c"),
                                      new ArrayValue(new Value[]{ null, StringValue.get("1") }))})})})));
   }

   @Test
   public void parses_jquery_param_strings()
   {
      QueryStringParser parser = new QueryStringParser();
      // filter[0][]=int1&filter[0][]==&filter[0][]=77&filter[]=and&
      // filter[2][]=int2&filter[2][]==&filter[2][]=8'
      Assertions.assertEquals(parser.parse("filter%5B0%5D%5B%5D=int1&filter%5B0%5D%5B%5D=%3D&" +
                                              "filter%5B0%5D%5B%5D=77&filter%5B%5D=and&filter%5B2%5D%5B%5D" +
                                              "=int2&filter%5B2%5D%5B%5D=%3D&filter%5B2%5D%5B%5D=8"),
                              new DictValue().append(StringValue.get("filter"),
                                                     new ArrayValue(new Value[]{
                                                        new ArrayValue(new Value[]{
                                                           StringValue.get("int1"), StringValue.get("="),
                                                           StringValue.get("77")}),
                                                        StringValue.get("and"),
                                                        new ArrayValue(new Value[]{
                                                           StringValue.get("int2"), StringValue.get("="),
                                                           StringValue.get("8")})})));
   }

   @Test
   public void continues_parsing_when_no_parent_is_found()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserSNH = new QueryStringParser(new ParserOptions().strictNullHandling(true));
      Assertions.assertEquals(parser.parse("[]=&a=b"),
                              new DictValue().append(IntValue.get(0),
                                                     StringValue.get(""))
                                             .append(StringValue.get("a"), StringValue.get("b")));
      Assertions.assertEquals(parserSNH.parse("[]&a=b"),
                              new DictValue().append(IntValue.get(0),
                                                     NullValue.get())
                                             .append(StringValue.get("a"), StringValue.get("b")));
      Assertions.assertEquals(parserSNH.parse("[foo]=bar"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("bar")));

   }

   @Test
   public void does_not_error_when_parsing_a_very_long_array()
   {
      QueryStringParser parser = new QueryStringParser();
      StringBuilder atom = new StringBuilder("a[] = a");
      while (atom.length() < 120 * 1024) {
         atom.append("&").append(atom);
      }
      final String fAtom = atom.toString();
      Assertions.assertDoesNotThrow(() -> parser.parse(fAtom));
   }

   @Test
   public void parses_a_string_with_an_alternative_string_delimiter()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().delimiter(";"));
      QueryStringParser parser2 = new QueryStringParser(new ParserOptions().delimiter("[;,] *"));
      Assertions.assertEquals(parser.parse("a=b;c=d"),
                              new DictValue().append(StringValue.get("a"), StringValue.get("b"))
                                             .append(StringValue.get("c"), StringValue.get("d")));
      Assertions.assertEquals(parser2.parse("a=b; c=d"),
                              new DictValue().append(StringValue.get("a"), StringValue.get("b"))
                                             .append(StringValue.get("c"), StringValue.get("d")));
   }

   @Test
   public void allows_overriding_parameter_limit()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().parameterLimit(1));
      Assertions.assertEquals(parser.parse("a=b&c=d"),
                              new DictValue().append(StringValue.get("a"), StringValue.get("b")));
   }

   @Test
   public void allows_overriding_array_limit()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().arrayLimit(-1));
      Assertions.assertEquals(parser.parse("a[0]=b"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(0),
                                                                            StringValue.get("b"))));
      Assertions.assertEquals(parser.parse("a[-1]=b"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(-1),
                                                                            StringValue.get("b"))));
      Assertions.assertEquals(parser.parse("a[0]=b&a[1]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(0),
                                                                            StringValue.get("b"))
                                                                    .append(IntValue.get(1),
                                                                            StringValue.get("c"))));
   }
}