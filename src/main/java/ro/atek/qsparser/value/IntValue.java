package ro.atek.qsparser.value;

/**
 * Special type of value which can represent an effective int. This kind
 * of value is used just to allow an automatic parsing of some dictionary
 * keys into integers. This only complicates the dictionary keys, maybe it
 * should be dropped.
 * <p>
 * This value can't be merged into other values.
 */
public final class IntValue
implements Value,
           DictKey
{
   /** The underlying int value wrapped by this class */
   private final int value;

   /**
    * Basic wrapping constructor.
    *
    * @param   value
    *          The int value to be wrapped.
    */
   public IntValue(int value)
   {
      this.value = value;
   }

   /**
    * Convenient static method to create {@code IntValue} instances.
    * This works like a mini-factory, allowing future caching or proxies.
    *
    * @param   value
    *          The int to be wrapped.
    *
    * @return  An int value wrapping the provided int.
    */
   public static IntValue get(int value)
   {
      return new IntValue(value);
   }

   /**
    * Easy way of accessing the wrapped resource.
    *
    * @return  The integer wrapped by this value.
    */
   public int intern()
   {
      return value;
   }

   /**
    * Retrieve the value type of this.
    *
    * @return   The integer type.
    */
   @Override
   public ValueType getType()
   {
      return ValueType.INT;
   }

   /**
    * Convenient representation of this value.
    *
    * @return  Simply the containing int.
    */
   @Override
   public String toString()
   {
      return String.valueOf(value);
   }

   /**
    * Implementation of the equal check. This should return {@code true}
    * only if the other object is a {@code IntValue} and wraps the
    * exact same int.
    *
    * @param    other
    *           The other object to be used for the equal check.
    *
    * @return   {@code true} only when the other value is a {@code IntValue}
    *           and wraps the exact same int.
    */
   @Override
   public boolean equals(Object other)
   {
      if (this == other) return true;
      if (!(other instanceof IntValue)) return false;
      return this.value == ((IntValue) other).value;
   }

   /**
    * A hash code implementation as this type in value can
    * be used as a dictionary key.
    *
    * @return   The hash code of the underlying integer.
    */
   @Override
   public int hashCode()
   {
      return Integer.valueOf(value).hashCode();
   }
}
