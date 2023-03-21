package ro.atek.qsparser.value;

import ro.atek.qsparser.StringifyOptions;

import java.util.Collections;
import java.util.List;

/**
 * Most common type of value parsed from a query string. A {@code StringValue}
 * is able to store a string reference. Basically, it is a simple wrapper over
 * {@code String} which allows the implementation of {@code Value}. Also,
 * this kind of value is able to represent a dictionary key.
 */
public class StringValue
implements Value,
           DictKey
{
   /** Convenient instance representing the empty string */
   public static final StringValue EMPTY = new StringValue("");

   /** The underlying data of the parsed string value */
   private final String value;

   /**
    * Basic constructor which wraps a string value. Note that we can't wrap
    * a null value here, so we implicitly convert it to an empty string.
    * A null value should be represented using {@link NullValue}
    *
    * @param   value
    *          The string value to be wrapped.
    */
   public StringValue(String value)
   {
      this.value = value == null ? "" : value;
   }

   /**
    * Convenient static method to create {@code StringValue} instances.
    * This works like a mini-factory, allowing future caching or proxies.
    *
    * @param   value
    *          The string to be wrapped.
    *
    * @return  A string value wrapping the provided string.
    */
   public static StringValue get(String value)
   {
      return new StringValue(value);
   }

   /**
    * Retrieve the internal string value.
    *
    * @return   The string wrapped by this representation.
    */
   public String intern()
   {
      return value;
   }


   /**
    * Represent the value as a query string. This is different from
    * {@code toString} in the sense that the result can be parsed
    * back as this value.
    *
    * @return   A query string representation of this value.
    */
   @Override
   public List<QueryStringEntry> stringify(String key, StringifyOptions options)
   {
      return Collections.singletonList(new QueryStringEntry(key, intern()));
   }

   /**
    * Retrieve the value type of this.
    *
    * @return   The string type.
    */
   @Override
   public ValueType getType()
   {
      return ValueType.STRING;
   }

   /**
    * Particular use-case in which a value need to be reinterpreted
    * as a numeric value. This is widely used for strings that look like
    * plain &#9312; and should be interpreted as a specific symbol.
    *
    * @return   The value in which all nested string values are
    *           reinterpreted based on the detected char code.
    */
   @Override
   public Value interpretAsNumeric()
   {
      String str = intern();
      if (!(str.startsWith("&#") && str.endsWith(";")))
      {
         return this;
      }

      str = str.substring(2, str.length() - 1);
      try
      {
         int idx = Integer.parseInt(str);
         return StringValue.get("" + Character.toChars(idx)[0]);
      }
      catch (NumberFormatException e)
      {
         // let it be
      }

      return this;
   }

   /**
    * Convenient representation of this value.
    *
    * @return  Simply the containing string.
    */
   @Override
   public String toString()
   {
      return value;
   }

   /**
    * Implementation of the equal check. This should return {@code true}
    * only if the other object is a {@code StringValue} and wraps the
    * exact same string.
    *
    * @param    other
    *           The other object to be used for the equal check.
    *
    * @return   {@code true} only when the other value is a {@code StringValue}
    *           and wraps the exact same string.
    */
   @Override
   public boolean equals(Object other)
   {
      if (this == other) return true;
      if (!(other instanceof StringValue)) return false;
      return value.equals(((StringValue) other).value);
   }

   /**
    * Plain implementation of the {@code hashCode} method. This
    * is required as this type a value can be a cache key.
    *
    * @return   The hash code of this instance based on the underlying
    *           string.
    */
   @Override
   public int hashCode()
   {
      return value.hashCode();
   }
}
