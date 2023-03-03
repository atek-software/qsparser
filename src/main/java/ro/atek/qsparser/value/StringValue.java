package ro.atek.qsparser.value;

/**
 * Most common type of value parsed from a query string. A {@code StringValue}
 * is able to store a string reference. Basically, it is a simple wrapper over
 * {@code String} which allows the implementation of {@code Value}. Also,
 * this kind of value is able to represent a dictionary key.
 */
public final class StringValue
implements Value,
           DictKey
{
   /** The underlying data of the parsed string value */
   private final String value;

   /**
    * Basic constructor which wraps a string value.
    *
    * @param   value
    *          The string value to be wrapped.
    */
   public StringValue(String value)
   {
      this.value = value;
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
    * Merge another value with this one. This returns a resulting
    * value and does not alter the operands. The result depends
    * on the type of the other value.
    * <p>
    * Only merging with an array is possible right now, resulting
    * in appending this value at the front-end of the array.
    *
    * @param   value
    *          Another value to be merged into this.
    *
    * @return  The result of the merging this with another arbitrary value.
    */
   @Override
   public Value merge(Value value)
   {
      if (!(value instanceof ArrayValue))
      {
         throw new RuntimeException("Not implemented yet!");
      }
      return new ArrayValue(this, value);
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
