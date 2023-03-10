package ro.atek.qsparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ro.atek.qsparser.decoder.Decoder;
import ro.atek.qsparser.decoder.DefaultDecoder;
import ro.atek.qsparser.value.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

class QueryStringParserTest
{
   @Test
   void parse_a_simple_string()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserSNH = new QueryStringParser(new ParserOptions().setStrictNullHandling(true));

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
   void arrays_on_the_same_key()
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
   void allow_dot_notation()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserAD = new QueryStringParser(new ParserOptions().setAllowDots(true));
      Assertions.assertEquals(parser.parse("a.b=c"),
                              new DictValue().append(StringValue.get("a.b"), StringValue.get("c")));
      Assertions.assertEquals(parserAD.parse("a.b=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(StringValue.get("b"),
                                                                            StringValue.get("c"))));
   }

   @Test
   void depth_parsing()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserD1 = new QueryStringParser(new ParserOptions().setDepth(1));
      QueryStringParser parserD0 = new QueryStringParser(new ParserOptions().setDepth(0));
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
   void parses_an_explicit_array()
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
   void parses_a_mix_of_simple_and_explicit_arrays()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parser20 = new QueryStringParser(new ParserOptions().setArrayLimit(20));
      QueryStringParser parser0 = new QueryStringParser(new ParserOptions().setArrayLimit(0));
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
   void parses_a_nested_array()
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
   void allows_to_specify_array_indices()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parser20 = new QueryStringParser(new ParserOptions().setArrayLimit(20));
      QueryStringParser parser0 = new QueryStringParser(new ParserOptions().setArrayLimit(0));
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
   void limits_specific_array_indices_to_arrayLimit()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parser20 = new QueryStringParser(new ParserOptions().setArrayLimit(20));
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
   void supports_keys_that_begin_with_a_number()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("a[12b]=c"),
                              new DictValue().append(StringValue.get("a"),
                                 new DictValue().append(StringValue.get("12b"), StringValue.get("c"))));
   }

   @Test
   void supports_encoded_equal_signs()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("he%3Dllo=th%3Dere"),
                              new DictValue().append(StringValue.get("he=llo"),
                                                     StringValue.get("th=ere")));
   }

   @Test
   void is_ok_with_url_encoded_strings()
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
   void allows_brackets_in_the_value()
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
   void allows_empty_values()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse(""), new DictValue());
      Assertions.assertEquals(parser.parse(null), new DictValue());
   }

   @Test
   void transforms_arrays_to_objects()
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
   void transforms_arrays_to_objects_dot_notation()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setAllowDots(true));
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
   void correctly_prunes_undefined_values()
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
   void supports_malformed_uri_characters()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserSNH = new QueryStringParser(new ParserOptions().setStrictNullHandling(true));
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
   void does_not_produce_empty_keys()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("_r=1&"),
                              new DictValue().append(StringValue.get("_r"),
                                                     StringValue.get("1")));
   }

   @Test
   void parses_arrays_of_objects()
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
   void allows_for_empty_strings_in_arrays()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserSNH20 = new QueryStringParser(new ParserOptions().setStrictNullHandling(true)
                                                                               .setArrayLimit(20));
      QueryStringParser parserSNH0 = new QueryStringParser(new ParserOptions().setStrictNullHandling(true)
                                                                               .setArrayLimit(0));
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
   void compacts_sparse_arrays()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setArrayLimit(20));
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
   void parses_sparse_arrays()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setAllowSparse(true));
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
   void parses_jquery_param_strings()
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
   void continues_parsing_when_no_parent_is_found()
   {
      QueryStringParser parser = new QueryStringParser();
      QueryStringParser parserSNH = new QueryStringParser(new ParserOptions().setStrictNullHandling(true));
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
   void does_not_error_when_parsing_a_very_long_array()
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
   void parses_a_string_with_an_alternative_string_delimiter()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setDelimiter(";"));
      QueryStringParser parser2 = new QueryStringParser(new ParserOptions().setDelimiter("[;,] *"));
      Assertions.assertEquals(parser.parse("a=b;c=d"),
                              new DictValue().append(StringValue.get("a"), StringValue.get("b"))
                                             .append(StringValue.get("c"), StringValue.get("d")));
      Assertions.assertEquals(parser2.parse("a=b; c=d"),
                              new DictValue().append(StringValue.get("a"), StringValue.get("b"))
                                             .append(StringValue.get("c"), StringValue.get("d")));
   }

   @Test
   void allows_overriding_parameter_limit()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setParameterLimit(1));
      QueryStringParser parser2 = new QueryStringParser(new ParserOptions().setParameterLimit(Integer.MAX_VALUE));
      Assertions.assertEquals(parser.parse("a=b&c=d"),
                              new DictValue().append(StringValue.get("a"), StringValue.get("b")));
      Assertions.assertEquals(parser2.parse("a=b&c=d"),
                              new DictValue().append(StringValue.get("a"), StringValue.get("b"))
                                             .append(StringValue.get("c"), StringValue.get("d")));
   }

   @Test
   void allows_overriding_array_limit()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setArrayLimit(-1));
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

   @Test
   void allows_disabling_array_parsing()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setParseArrays(false));
      Assertions.assertEquals(parser.parse("a[0]=b&a[1]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(0),
                                                                            StringValue.get("b"))
                                                                    .append(IntValue.get(1),
                                                                            StringValue.get("c"))));
      Assertions.assertEquals(parser.parse("a[]=b"),
                              new DictValue().append(StringValue.get("a"),
                                                     new DictValue().append(IntValue.get(0),
                                                                            StringValue.get("b"))));
   }

   @Test
   void allows_for_query_string_prefix()
   {
      QueryStringParser parseriqf = new QueryStringParser(new ParserOptions().setIgnoreQueryPrefix(true));
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setIgnoreQueryPrefix(false));
      Assertions.assertEquals(parseriqf.parse("?foo=bar"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("bar")));
      Assertions.assertEquals(parseriqf.parse("foo=bar"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("bar")));
      Assertions.assertEquals(parser.parse("?foo=bar"),
                              new DictValue().append(StringValue.get("?foo"), StringValue.get("bar")));
   }

   @Test
   void parses_string_with_comma_as_array_divider()
   {
      QueryStringParser simpleParser = new QueryStringParser();
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setComma(true));
      QueryStringParser parserSNH = new QueryStringParser(
         new ParserOptions().setComma(true).setStrictNullHandling(true));
      Assertions.assertEquals(parser.parse("foo=bar,tee"),
                              new DictValue().append(StringValue.get("foo"), new ArrayValue(new Value[] {
                                 StringValue.get("bar"),
                                 StringValue.get("tee")})));
      Assertions.assertEquals(parser.parse("foo[bar]=coffee,tee"),
                              new DictValue().append(StringValue.get("foo"),
                                   new DictValue().append(StringValue.get("bar"), new ArrayValue(new Value[] {
                                       StringValue.get("coffee"),
                                       StringValue.get("tee")}))));
      Assertions.assertEquals(parser.parse("foo="),
                              new DictValue().append(StringValue.get("foo"), StringValue.EMPTY));
      Assertions.assertEquals(parser.parse("foo"),
                              new DictValue().append(StringValue.get("foo"), StringValue.EMPTY));
      Assertions.assertEquals(parserSNH.parse("foo"),
                              new DictValue().append(StringValue.get("foo"), NullValue.get()));

      Assertions.assertEquals(simpleParser.parse("a[0]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("c") })));
      Assertions.assertEquals(simpleParser.parse("a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("c") })));
      Assertions.assertEquals(parser.parse("a[]=c"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("c") })));

      Assertions.assertEquals(simpleParser.parse("a[0]=c&a[1]=d"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("c"),
                                                     StringValue.get("d") })));
      Assertions.assertEquals(simpleParser.parse("a[]=c&a[]=d"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("c"),
                                                        StringValue.get("d") })));
      Assertions.assertEquals(parser.parse("a[]=c&a[]=d"),
                              new DictValue().append(StringValue.get("a"),
                                                     new ArrayValue(new Value[] { StringValue.get("c"),
                                                        StringValue.get("d") })));
   }

   @Test
   void use_number_decoder()
   {
      Decoder decoder = (content, charset, type) ->
      {
         try
         {
            int value = Integer.parseInt(content);
            return "[" + value + "]";
         }
         catch (NumberFormatException e)
         {
            return DefaultDecoder.DEFAULT_DECODER.decode(content, charset, type);
         }
      };
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setDecoder(decoder));
      Assertions.assertEquals(parser.parse("foo=1"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("[1]")));
      Assertions.assertEquals(parser.parse("foo=1.0"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("1.0")));
      Assertions.assertEquals(parser.parse("foo=0"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("[0]")));
   }

   @Test
   void parses_comma_delimited_array()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setComma(true));
      Assertions.assertEquals(parser.parse("foo=a%2Cb"),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("a,b")));
      Assertions.assertEquals(parser.parse("foo=a%2C%20b,d"),
                              new DictValue().append(StringValue.get("foo"), new ArrayValue(new Value[] {
                                 StringValue.get("a, b"), StringValue.get("d")})));
      Assertions.assertEquals(parser.parse("foo=a%2C%20b,c%2C%20d"),
                              new DictValue().append(StringValue.get("foo"), new ArrayValue(new Value[] {
                                 StringValue.get("a, b"), StringValue.get("c, d")})));
   }


   @Test
   void does_not_crash_when_parsing_deep_objects()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setDepth(5000));
      StringBuilder str = new StringBuilder("foo");
      for (int i = 0; i < 5000; i++)
      {
         str.append("[p]");
      }
      str.append("=bar");
      Value[] val = new Value[1];
      Assertions.assertDoesNotThrow(() -> val[0] = parser.parse(str.toString()));
      int depth = 0;
      StringValue key = StringValue.get("p");
      Assertions.assertInstanceOf(DictValue.class, val[0]);
      Assertions.assertTrue(((DictValue) val[0]).containsKey(StringValue.get("foo")));
      val[0] = ((DictValue) val[0]).get(StringValue.get("foo"));
      while (val[0].getType() == ValueType.DICT && ((DictValue) val[0]).containsKey(key))
      {
         val[0] = ((DictValue) val[0]).get(key);
         depth++;
      }
      Assertions.assertEquals(depth, 5000);
   }

   @Test
   void params_starting_with_a_closing_bracket()
   {
      QueryStringParser parser = new QueryStringParser();

      Assertions.assertEquals(parser.parse("]=toString"),
                              new DictValue().append(StringValue.get("]"), StringValue.get("toString")));
      Assertions.assertEquals(parser.parse("]]=toString"),
                              new DictValue().append(StringValue.get("]]"), StringValue.get("toString")));
      Assertions.assertEquals(parser.parse("]hello]=toString"),
                              new DictValue().append(StringValue.get("]hello]"), StringValue.get("toString")));
   }

   @Test
   void params_starting_with_a_starting_bracket()
   {
      QueryStringParser parser = new QueryStringParser();

      Assertions.assertEquals(parser.parse("[=toString"),
                              new DictValue().append(StringValue.get("["), StringValue.get("toString")));
      Assertions.assertEquals(parser.parse("[[=toString"),
                              new DictValue().append(StringValue.get("[["), StringValue.get("toString")));
      Assertions.assertEquals(parser.parse("[hello[=toString"),
                              new DictValue().append(StringValue.get("[hello["), StringValue.get("toString")));
   }

   @Test
   void add_keys_to_objects()
   {
      QueryStringParser parser = new QueryStringParser();

      Assertions.assertEquals(parser.parse("a[b]=c&a=d"),
                              new DictValue().append(StringValue.get("a"), new DictValue().append(
                                 StringValue.get("b"), StringValue.get("c")
                              ).append(StringValue.get("d"), BoolValue.TRUE)));
   }

   @Test
   void can_parse_with_custom_encoding()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setDecoder(
         (content, charset, type) -> {
            try
            {
               return URLDecoder.decode(content, "Shift-JIS");
            }
            catch (UnsupportedEncodingException e)
            {
               return content;
            }
         }));

      Assertions.assertEquals(parser.parse("%8c%a7=%91%e5%8d%e3%95%7b"),
                              new DictValue().append(StringValue.get("県"), StringValue.get("大阪府")));
   }

   @Test
   void does_not_mutate_the_options()
   {
      ParserOptions options = new ParserOptions();
      ParserOptions options2 = new ParserOptions();
      QueryStringParser parser = new QueryStringParser(options);
      parser.parse("a[b]=true");
      Assertions.assertEquals(options, options2);
   }

   @Test
   void parse_other_charset()
   {
      QueryStringParser parser = new QueryStringParser(
         new ParserOptions().setCharset(StandardCharsets.ISO_8859_1));
      Assertions.assertEquals(parser.parse("%A2=%BD"),
                              new DictValue().append(StringValue.get("¢"), StringValue.get("½")));
   }

   @Test
   void parse_charset_sentinel()
   {
      String urlEncodedCheckmarkInUtf8 = "%E2%9C%93";
      String urlEncodedOSlashInUtf8 = "%C3%B8";
      String urlEncodedNumCheckmark = "%26%2310003%3B";

      QueryStringParser parser = new QueryStringParser(
         new ParserOptions().setCharsetSentinel(true).setCharset(StandardCharsets.ISO_8859_1));
      QueryStringParser parserutf8 = new QueryStringParser(
         new ParserOptions().setCharsetSentinel(true).setCharset(StandardCharsets.UTF_8));
      QueryStringParser parserdefault = new QueryStringParser(
         new ParserOptions().setCharsetSentinel(true));
      Assertions.assertEquals(parser.parse("utf8=" + urlEncodedCheckmarkInUtf8 + "&" +
                                              urlEncodedOSlashInUtf8 + "=" + urlEncodedOSlashInUtf8),
                              new DictValue().append(StringValue.get("ø"), StringValue.get("ø")));
      Assertions.assertEquals(parserutf8.parse("utf8=" + urlEncodedNumCheckmark + "&" +
                                              urlEncodedOSlashInUtf8 + "=" + urlEncodedOSlashInUtf8),
                              new DictValue().append(StringValue.get("Ã¸"), StringValue.get("Ã¸")));
      Assertions.assertEquals(parserutf8.parse("a=" + urlEncodedOSlashInUtf8 + "&utf8=" +
                                                  urlEncodedNumCheckmark),
                              new DictValue().append(StringValue.get("a"), StringValue.get("Ã¸")));
      Assertions.assertEquals(parserutf8.parse("utf8=foo&" + urlEncodedOSlashInUtf8 + "=" +
                                                  urlEncodedOSlashInUtf8),
                              new DictValue().append(StringValue.get("ø"), StringValue.get("ø")));
      Assertions.assertEquals(parserdefault.parse("utf8=" + urlEncodedCheckmarkInUtf8 + "&" +
                                                  urlEncodedOSlashInUtf8 + "=" + urlEncodedOSlashInUtf8),
                              new DictValue().append(StringValue.get("ø"), StringValue.get("ø")));
      Assertions.assertEquals(parserdefault.parse("utf8=" + urlEncodedNumCheckmark + "&" +
                                                     urlEncodedOSlashInUtf8 + "=" + urlEncodedOSlashInUtf8),
                              new DictValue().append(StringValue.get("Ã¸"), StringValue.get("Ã¸")));
   }

   @Test
   void interpret_numeric_entities()
   {
      String urlEncodedNumSmiley = "%26%239786%3B";

      QueryStringParser parser = new QueryStringParser(
         new ParserOptions().setCharset(StandardCharsets.ISO_8859_1));
      QueryStringParser parserISO = new QueryStringParser(
         new ParserOptions().setCharset(StandardCharsets.ISO_8859_1).setInterpretNumericEntities(true));
      QueryStringParser parserUTF = new QueryStringParser(
         new ParserOptions().setCharset(StandardCharsets.UTF_8).setInterpretNumericEntities(true));
      Assertions.assertEquals(parserISO.parse("foo=" + urlEncodedNumSmiley),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("☺")));
      Assertions.assertEquals(parser.parse("foo=" + urlEncodedNumSmiley),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("&#9786;")));
      Assertions.assertEquals(parserUTF.parse("foo=" + urlEncodedNumSmiley),
                              new DictValue().append(StringValue.get("foo"), StringValue.get("&#9786;")));
   }

   @Test
   void allow_for_decoding_keys_and_values()
   {
      QueryStringParser parser = new QueryStringParser(new ParserOptions().setDecoder(
         (content, charset, type) -> {
            if (type == Decoder.ContentType.KEY)
               return DefaultDecoder.DEFAULT_DECODER.decode(content.toLowerCase(), charset, type);
            else
               return DefaultDecoder.DEFAULT_DECODER.decode(content.toUpperCase(), charset, type);
         }));
      Assertions.assertEquals(parser.parse("KeY=vAlUe"), new DictValue().append(
         StringValue.get("key"), StringValue.get("VALUE")));
   }

   @Test
   void proof_of_concept()
   {
      QueryStringParser parser = new QueryStringParser();
      Assertions.assertEquals(parser.parse("filters[name][:eq]=John&filters[age][:ge]=18&filters[age][:le]=60"),
                              new DictValue().append(StringValue.get("filters"),
                                new DictValue().append(StringValue.get("name"),
                                                       new DictValue().append(StringValue.get(":eq"),
                                                                              StringValue.get("John")))
                                               .append(StringValue.get("age"),
                                                       new DictValue().append(StringValue.get(":ge"),
                                                                              StringValue.get("18"))
                                                                      .append(StringValue.get(":le"),
                                                                              StringValue.get("60")))));
   }
}