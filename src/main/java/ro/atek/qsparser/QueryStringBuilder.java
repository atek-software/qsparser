package ro.atek.qsparser;

import ro.atek.qsparser.net.ContentType;
import ro.atek.qsparser.value.QueryStringEntry;
import ro.atek.qsparser.value.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Core helper used for generating query strings based on internal representations.
 */
public class QueryStringBuilder
{
   /** The configuration used when generating the query string */
   private final StringifyOptions options;

   /**
    * Basic constructor. This starts with the default configuration.
    */
   public QueryStringBuilder()
   {
      this(null);
   }

   /**
    * Constructor based on a specified configuration. Don't use this
    * if you are going to use the default settings anyway.
    *
    * @param   options
    *          The configuration to be used by this builder. {@code null}
    *          means use the default settings.
    */
   public QueryStringBuilder(StringifyOptions options)
   {
      this.options = options == null ? StringifyOptions.DEFAULT : options;
   }

   /**
    * Core method used to encode a value into a query string. The resulting string
    * is already encoded and, theoretically, parsing it back should result in the
    * same provided value.
    *
    * @param   value
    *          The root of the value to be converted into a query string.
    *
    * @return  A query string based on the provided internal representation.
    */
   public String stringify(Value value)
   {
      if (value == null)
      {
         return "";
      }

      List<QueryStringEntry> chunks = value.stringify("", options);
      String prefix = options.addQueryPrefix ? "?" : "";
      String queryString = chunks.stream()
                                 .filter((pair) -> pair.getKey() != null && !pair.getKey().isEmpty())
                                 .map(this::stringifyEntry)
                                 .collect(Collectors.joining(options.delimiter));
      return queryString.isEmpty() ? "" : prefix + queryString;
   }

   /**
    * Internal method used to ensure that an entry (key value pair) is
    * properly encoded, honoring the provided options.
    *
    * @param   pair
    *          The key value entry provided by the query string builder.
    *
    * @return  An encoded string representation of the entry
    */
   private String stringifyEntry(QueryStringEntry pair)
   {
      String key = encode(pair.getKey(), ContentType.KEY);
      if (pair.getValues() == null)
      {
         return key;
      }
      String val;
      if (options.encodeValuesOnly)
      {
         val = pair.getValues().stream()
                   .map((value) -> encode(value, ContentType.VALUE))
                   .collect(Collectors.joining(","));
      }
      else
      {
         val = encode(String.join(",", pair.getValues()), ContentType.VALUE);
      }
      return key + "=" + val;
   }

   /**
    * Simple mean of honoring the encoder provided by the options.
    *
    * @param   value
    *          The string value to be URL encoded.
    * @param   type
    *          The type of the value: key or value.
    *
    * @return  The encoded representation of the provided string.
    */
   private String encode(String value, ContentType type)
   {
      if (value == null)
      {
         return "";
      }
      if (options.encode &&
         ((type == ContentType.KEY && !options.encodeValuesOnly) || type == ContentType.VALUE))
      {
         return options.encoder.encode(value, options.charset, type);
      }
      else
      {
         return value;
      }
   }
}
