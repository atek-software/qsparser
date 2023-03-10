package ro.atek.qsparser.value;

/**
 * Special type of value which denotes the null representation.
 * This should not be confused with the lack of information. An
 * array value can have both {@code null} and instances of
 * {@code NullValue}. {@code null} is used to represent the lack
 * of information from the query string. {@code NullValue} is used
 * to represent a null value described in the query string explicitly.
 */
public final class NullValue
implements Value
{
   /** Store one single instance of the null value */
   public static final NullValue INSTANCE = new NullValue();

   /**
    * Convenient static method to access the singleton value.
    *
    * @return   The {@code NULL_VALUE} instance.
    */
   public static NullValue get()
   {
      return INSTANCE;
   }

   /**
    * Private constructor to disallow multiple instantiations of the
    * null value.
    */
   private NullValue()
   {

   }

   /**
    * Retrieve the value type of this.
    *
    * @return   The null type.
    */
   @Override
   public ValueType getType()
   {
      return ValueType.NULL;
   }

   /**
    * Convenient representation of this value.
    *
    * @return  Simply "null".
    */
   @Override
   public String toString()
   {
      return "null";
   }

   /**
    * Implementation of the equal check. This should return {@code true}
    * only if the other object is the same instance as this. Note that
    * {@code NullValue} is a singleton.
    *
    * @param    other
    *           The other object to be used for the equal check.
    *
    * @return   {@code true} only when the other object is the same
    *           instance as this.
    */
   @Override
   public boolean equals(Object other)
   {
      return other == this;
   }

   /**
    * Implementation of the hash code. This should return a consistent
    * value as this is a singleton anyway.
    *
    * @return   The hash code of a {@code null} value.
    */
   @Override
   public int hashCode()
   {
      return super.hashCode();
   }
}
