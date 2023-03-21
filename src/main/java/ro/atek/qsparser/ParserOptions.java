package ro.atek.qsparser;

import ro.atek.qsparser.net.Decoder;
import ro.atek.qsparser.net.DefaultDecoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration class for the parser. There are several options
 * which can be set per parser, like the delimiter, allowing dots
 * instead of square brackets, parameter limit or array limit.
 */
public class ParserOptions
{
   /**
    * A default instance of the parser options.
    * This should be used to avoid generating multiple default instances.
    */
   public static final ParserOptions DEFAULT = new ParserOptions();

   /** Allow using dot instead of square brackets in the key */
   public boolean allowDots = false;

   /** Allow un-compact arrays with {@code null} values */
   public boolean allowSparse = false;

   /** The maximum index accessible in any array */
   public int arrayLimit = 20;

   /** The charset used when decoding */
   public Charset charset = StandardCharsets.UTF_8;

   /** Parse a special marker at the beginning of the query string defining the charset */
   public boolean charsetSentinel = false;

   /** Allow the use of comma in value to define arrays. */
   public boolean comma = false;

   /** The decoder used for decoding the keys and values. This uses the defined charset. */
   public Decoder decoder = DefaultDecoder.INSTANCE;

   /** The delimiter of the key, value entries */
   public String delimiter = "&";

   /** The maximum depth of the processed dictionary */
   public int depth = 5;

   /** Remove the question mark at the beginning if exists */
   public boolean ignoreQueryPrefix = false;

   /** Special procedure for ISO-8859-1 allowing to parse the numerics into proper characters */
   public boolean interpretNumericEntities = false;

   /** Maximum number of key value pairs allowed */
   public int parameterLimit = 1000;

   /** Allow the parsing of keys with integer indexes as arrays */
   public boolean parseArrays = true;

   /** Dictionaries with integer keys will have their keys parsed as integers */
   public boolean parseIntKeys = true;

   /** {@code true} means that empty values are null, otherwise they are parsed as an empty string */
   public boolean strictNullHandling = false;

   /**
    * Implicit constructor. Use {@link #DEFAULT} if going to
    * use the default settings.
    */
   public ParserOptions()
   {

   }

   /**
    * Set the {@link #strictNullHandling} option.
    *
    * @param  strictNullHandling
    *         The value for {@link #strictNullHandling}.
    *
    * @return This instance.
    */
   public ParserOptions setStrictNullHandling(boolean strictNullHandling)
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
   public ParserOptions setAllowDots(boolean allowDots)
   {
      this.allowDots = allowDots;
      return this;
   }

   /**
    * Set the {@link #depth} option.
    *
    * @param  depth
    *         The value for {@link #depth}.
    *
    * @return This instance.
    */
   public ParserOptions setDepth(int depth)
   {
      this.depth = depth;
      return this;
   }

   /**
    * Set the {@link #arrayLimit} option.
    *
    * @param  arrayLimit
    *         The value for {@link #arrayLimit}.
    *
    * @return This instance.
    */
   public ParserOptions setArrayLimit(int arrayLimit)
   {
      this.arrayLimit = arrayLimit;
      return this;
   }

   /**
    * Set the {@link #allowSparse} option.
    *
    * @param  allowSparse
    *         The value for {@link #allowSparse}.
    *
    * @return This instance.
    */
   public ParserOptions setAllowSparse(boolean allowSparse)
   {
      this.allowSparse = allowSparse;
      return this;
   }

   /**
    * Set the {@link #delimiter} option.
    *
    * @param  delimiter
    *         The value for {@link #delimiter}.
    *
    * @return This instance.
    */
   public ParserOptions setDelimiter(String delimiter)
   {
      this.delimiter = delimiter;
      return this;
   }

   /**
    * Set the {@link #parameterLimit} option.
    *
    * @param  parameterLimit
    *         The value for {@link #parameterLimit}.
    *
    * @return This instance.
    */
   public ParserOptions setParameterLimit(int parameterLimit)
   {
      this.parameterLimit = parameterLimit;
      return this;
   }

   /**
    * Set the {@link #charset} option.
    *
    * @param  charset
    *         The value for {@link #charset}.
    *
    * @return This instance.
    */
   public ParserOptions setCharset(Charset charset)
   {
      this.charset = charset;
      return this;
   }

   /**
    * Set the {@link #charsetSentinel} option.
    *
    * @param  charsetSentinel
    *         The value for {@link #charsetSentinel}.
    *
    * @return This instance.
    */
   public ParserOptions setCharsetSentinel(boolean charsetSentinel)
   {
      this.charsetSentinel = charsetSentinel;
      return this;
   }

   /**
    * Set the {@link #comma} option.
    *
    * @param  comma
    *         The value for {@link #comma}.
    *
    * @return This instance.
    */
   public ParserOptions setComma(boolean comma)
   {
      this.comma = comma;
      return this;
   }

   /**
    * Set the {@link #decoder} option.
    *
    * @param  decoder
    *         The value for {@link #decoder}.
    *
    * @return This instance.
    */
   public ParserOptions setDecoder(Decoder decoder)
   {
      this.decoder = decoder;
      return this;
   }

   /**
    * Set the {@link #parseArrays} option.
    *
    * @param  parseArrays
    *         The value for {@link #parseArrays}.
    *
    * @return This instance.
    */
   public ParserOptions setParseArrays(boolean parseArrays)
   {
      this.parseArrays = parseArrays;
      return this;
   }

   /**
    * Set the {@link #parseIntKeys} option.
    *
    * @param  parseIntKeys
    *         The value for {@link #parseIntKeys}.
    *
    * @return This instance.
    */
   public ParserOptions setParseIntKeys(boolean parseIntKeys)
   {
      this.parseIntKeys = parseIntKeys;
      return this;
   }

   /**
    * Set the {@link #interpretNumericEntities} option.
    *
    * @param  interpretNumericEntities
    *         The value for {@link #interpretNumericEntities}.
    *
    * @return This instance.
    */
   public ParserOptions setInterpretNumericEntities(boolean interpretNumericEntities)
   {
      this.interpretNumericEntities = interpretNumericEntities;
      return this;
   }

   /**
    * Set the {@link #ignoreQueryPrefix} option.
    *
    * @param  ignoreQueryPrefix
    *         The value for {@link #ignoreQueryPrefix}.
    *
    * @return This instance.
    */
   public ParserOptions setIgnoreQueryPrefix(boolean ignoreQueryPrefix)
   {
      this.ignoreQueryPrefix = ignoreQueryPrefix;
      return this;
   }

   /**
    * Check if the provided object is a parser options instance with the same
    * options set.
    *
    * @param   o
    *          Another object used for equality check.
    *
    * @return  {@code true} if the provided object is a parser object instance with
    *          the same options set.
    */
   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ParserOptions that = (ParserOptions) o;
      return allowDots == that.allowDots &&
             allowSparse == that.allowSparse &&
             arrayLimit == that.arrayLimit &&
             charsetSentinel == that.charsetSentinel &&
             comma == that.comma &&
             depth == that.depth &&
             ignoreQueryPrefix == that.ignoreQueryPrefix &&
             parameterLimit == that.parameterLimit &&
             parseArrays == that.parseArrays &&
             parseIntKeys == that.parseIntKeys &&
             strictNullHandling == that.strictNullHandling &&
             charset.equals(that.charset) &&
             decoder == that.decoder &&
             delimiter.equals(that.delimiter);
   }

   /**
    * Compute the hash-code of the parser options.
    *
    * @return   the hash-code of the options.
    */
   @Override
   public int hashCode()
   {
      return Objects.hash(allowDots, allowSparse, arrayLimit, charset, charsetSentinel, comma, decoder,
                          delimiter, depth, ignoreQueryPrefix, parameterLimit, parseArrays, parseIntKeys,
                          strictNullHandling);
   }
}