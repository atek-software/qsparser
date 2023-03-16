package ro.atek.qsparser;

import ro.atek.qsparser.net.DefaultEncoder;
import ro.atek.qsparser.net.Encoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringifyOptions
{
   public static final StringifyOptions DEFAULT = new StringifyOptions();

   public boolean addQueryPrefix = false;
   public ArrayFormat arrayFormat = ArrayFormat.INDICES;
   public boolean allowDots = false;
   public Charset charset = StandardCharsets.UTF_8;
   public boolean charsetSentinel = false;
   public boolean commaRoundTrip = false;
   public String delimiter = "&";
   public String dictInArrayReplacement = "DictValue";
   public boolean encode = true;
   public Encoder encoder = DefaultEncoder.INSTANCE;
   public boolean encodeValuesOnly = false;
   public boolean indices = true;
//   public Format format =
//   public Formatter formatter =
//   public Function<DateTime, String> serializeDate = ;
   public boolean skipNulls = false;
   public boolean strictNullHandling = false;

   public StringifyOptions setEncoder(Encoder encoder)
   {
      this.encoder = encoder;
      return this;
   }

   public StringifyOptions setEncodeValuesOnly(boolean encodeValuesOnly)
   {
      this.encodeValuesOnly = encodeValuesOnly;
      return this;
   }

   public StringifyOptions setArrayFormat(ArrayFormat arrayFormat)
   {
      this.arrayFormat = arrayFormat;
      return this;
   }

   public StringifyOptions setAddQueryPrefix(boolean addQueryPrefix)
   {
      this.addQueryPrefix = addQueryPrefix;
      return this;
   }

   public StringifyOptions setStrictNullHandling(boolean strictNullHandling)
   {
      this.strictNullHandling = strictNullHandling;
      return this;
   }

   public StringifyOptions setAllowDots(boolean allowDots)
   {
      this.allowDots = allowDots;
      return this;
   }

   public StringifyOptions setSkipsNulls(boolean skipNulls)
   {
      this.skipNulls = skipNulls;
      return this;
   }

   public StringifyOptions setIndices(boolean indices)
   {
      this.indices = indices;
      return this;
   }

   public StringifyOptions setCommaRoundTrip(boolean commaRoundTrip)
   {
      this.commaRoundTrip = commaRoundTrip;
      return this;
   }

   public StringifyOptions setEncode(boolean encode)
   {
      this.encode = encode;
      return this;
   }
}
