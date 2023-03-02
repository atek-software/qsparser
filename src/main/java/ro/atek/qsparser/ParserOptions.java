package ro.atek.qsparser;

import ro.atek.qsparser.decoder.Decoder;
import ro.atek.qsparser.decoder.DefaultDecoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Configuration class for the parser. There are several options
 * which can be set per parser, like the delimiter, allowing dots
 * instead of square brackets, parameter limit or array limit.
 */
class ParserOptions
{
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
   public Decoder decoder = new DefaultDecoder();

   /** The delimiter of the key, value entries */
   public String delimiter = "&";

   /** The maximum depth of the processed dictionary */
   public int depth = 5;

   /** Remove the question mark at the beginning if exists */
   public boolean ignoreQueryPrefix = false;

   /** Maximum number of key value pairs allowed */
   public int parameterLimit = 1000;

   /** Allow the parsing of keys with integer indexes as arrays */
   public boolean parseArrays = true;

   /** Dictionaries with integer keys will have their keys parsed as integers */
   public boolean parseIntKeys = true;

   /** Not used */
   public boolean plainObjects = false;

   /** {@code true} means that empty values are null, otherwise they are parsed as an empty string */
   public boolean strictNullHandling = false;

   /**
    * Set the {@link #strictNullHandling} option.
    *
    * @param  strictNullHandling
    *         The value for {@link #strictNullHandling}.
    *
    * @return This instance.
    */
   public ParserOptions strictNullHandling(boolean strictNullHandling)
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
   public ParserOptions allowDots(boolean allowDots)
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
   public ParserOptions depth(int depth)
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
   public ParserOptions arrayLimit(int arrayLimit)
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
   public ParserOptions allowSparse(boolean allowSparse)
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
   public ParserOptions delimiter(String delimiter)
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
   public ParserOptions parameterLimit(int parameterLimit)
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
   public ParserOptions charset(Charset charset)
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
   public ParserOptions charsetSentinel(boolean charsetSentinel)
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
   public ParserOptions comma(boolean comma)
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
   public ParserOptions decoder(Decoder decoder)
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
   public ParserOptions parseArrays(boolean parseArrays)
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
   public ParserOptions parseIntKeys(boolean parseIntKeys)
   {
      this.parseIntKeys = parseIntKeys;
      return this;
   }
}