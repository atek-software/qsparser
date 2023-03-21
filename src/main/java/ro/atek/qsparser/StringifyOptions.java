package ro.atek.qsparser;

import ro.atek.qsparser.net.DefaultEncoder;
import ro.atek.qsparser.net.Encoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Configuration class for the query string builder. There are several options
 * which can be set per builder, like the delimiter, encoding with dots
 * instead of square brackets, array format or charset.
 */
public class StringifyOptions
{
   /**
    * A default instance of the query string builder options.
    * This should be used to avoid generating multiple default instances.
    */
   public static final StringifyOptions DEFAULT = new StringifyOptions();

   /** This should be set if a ? prefix should be appended */
   public boolean addQueryPrefix = false;

   /** The format in which the arrays are encoded into the query string. See {@link ArrayFormat}.*/
   public ArrayFormat arrayFormat = ArrayFormat.INDICES;

   /** Force the encoding with dots instead of square brackets. */
   public boolean allowDots = false;

   /** The charset to be used when encoding to query string */
   public Charset charset = StandardCharsets.UTF_8;

   /** Ensure that {@link ArrayFormat#COMMA} over singletons generate a square bracket suffix */
   public boolean commaRoundTrip = false;

   /** The delimiter between query string entries - key, value pairs */
   public String delimiter = "&";

   /** The replacement used when encoding a dictionary inside an array with {@link ArrayFormat#COMMA} */
   public String dictInArrayReplacement = "DictValue";

   /** Set if the final string should honor the encoder */
   public boolean encode = true;

   /** The encoder to be used */
   public Encoder encoder = DefaultEncoder.INSTANCE;

   /** This should be set only if the primitive values should be encoded */
   public boolean encodeValuesOnly = false;

   /** Avoid any suffix when encoding arrays, including index specification */
   public boolean indices = true;

   /** Omit dictionary entries which have a null value */
   public boolean skipNulls = false;

   /** Encode {@code null} as empty string or omit key if {@link #skipNulls} is set */
   public boolean strictNullHandling = false;

   /**
    * Implicit constructor. Use {@link #DEFAULT} if going to
    * use the default settings.
    */
   public StringifyOptions()
   {

   }

   /**
    * Set the {@link #encoder} option.
    *
    * @param  encoder
    *         The value for {@link #encoder}.
    *
    * @return This instance.
    */
   public StringifyOptions setEncoder(Encoder encoder)
   {
      this.encoder = encoder;
      return this;
   }

   /**
    * Set the {@link #encodeValuesOnly} option.
    *
    * @param  encodeValuesOnly
    *         The value for {@link #encodeValuesOnly}.
    *
    * @return This instance.
    */
   public StringifyOptions setEncodeValuesOnly(boolean encodeValuesOnly)
   {
      this.encodeValuesOnly = encodeValuesOnly;
      return this;
   }

   /**
    * Set the {@link #arrayFormat} option.
    *
    * @param  arrayFormat
    *         The value for {@link #arrayFormat}.
    *
    * @return This instance.
    */
   public StringifyOptions setArrayFormat(ArrayFormat arrayFormat)
   {
      this.arrayFormat = arrayFormat;
      return this;
   }

   /**
    * Set the {@link #addQueryPrefix} option.
    *
    * @param  addQueryPrefix
    *         The value for {@link #addQueryPrefix}.
    *
    * @return This instance.
    */
   public StringifyOptions setAddQueryPrefix(boolean addQueryPrefix)
   {
      this.addQueryPrefix = addQueryPrefix;
      return this;
   }

   /**
    * Set the {@link #strictNullHandling} option.
    *
    * @param  strictNullHandling
    *         The value for {@link #strictNullHandling}.
    *
    * @return This instance.
    */
   public StringifyOptions setStrictNullHandling(boolean strictNullHandling)
   {
      this.strictNullHandling = strictNullHandling;
      return this;
   }

   /**
    * Set the {@link #allowDots} option.
    *
    * @param  allowDots
    *         The value for {@link #allowDots}.
    *
    * @return This instance.
    */
   public StringifyOptions setAllowDots(boolean allowDots)
   {
      this.allowDots = allowDots;
      return this;
   }

   /**
    * Set the {@link #skipNulls} option.
    *
    * @param  skipNulls
    *         The value for {@link #skipNulls}.
    *
    * @return This instance.
    */
   public StringifyOptions setSkipsNulls(boolean skipNulls)
   {
      this.skipNulls = skipNulls;
      return this;
   }

   /**
    * Set the {@link #indices} option.
    *
    * @param  indices
    *         The value for {@link #indices}.
    *
    * @return This instance.
    */
   public StringifyOptions setIndices(boolean indices)
   {
      this.indices = indices;
      return this;
   }

   /**
    * Set the {@link #commaRoundTrip} option.
    *
    * @param  commaRoundTrip
    *         The value for {@link #commaRoundTrip}.
    *
    * @return This instance.
    */
   public StringifyOptions setCommaRoundTrip(boolean commaRoundTrip)
   {
      this.commaRoundTrip = commaRoundTrip;
      return this;
   }

   /**
    * Set the {@link #encode} option.
    *
    * @param  encode
    *         The value for {@link #encode}.
    *
    * @return This instance.
    */
   public StringifyOptions setEncode(boolean encode)
   {
      this.encode = encode;
      return this;
   }
}
