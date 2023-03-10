package ro.atek.qsparser.value;

/**
 * General interface for all internal values. To support
 * an internal representation of the query string, we need
 * primitive values like string and integer. Also, we need
 * compound values like arrays and dictionaries.
 */
public interface Value
{
   /**
    * A mean of combining two values into a single one. This is
    * needed for cases in which we need to compute each query
    * string component individually and then merge it into a
    * single value. This is also useful for run-time changes.
    *
    * @param   other
    *          Another value to be merged into this.
    *
    * @return  The resulting value. Note that the merged operands
    *          won't be changed, but can be referenced from the returned
    *          value.
    */
   default Value merge(Value other)
   {
      if (other == null)
      {
         return this;
      }
      return new ArrayValue(this, other);
   }

   /**
    * Return a compacted version of the current value. Compact operation
    * is required by use-cases in which we expect that the compound data
    * values don't have unknown values inside.
    *
    * @return   The resulting value after compact.
    */
   default Value compact()
   {
      return this;
   }

   /**
    * Retrieve the value type of this.
    *
    * @return   The value type of the representation.
    */
   ValueType getType();

   /**
    * Particular use-case in which a value need to be reinterpreted
    * as a numeric value. This is widely used for strings that look like
    * plain &#9312; and should be interpreted as a specific symbol.
    *
    * @return   The value in which all nested string values are
    *           reinterpreted based on the detected char code.
    */
   default Value interpretAsNumeric()
   {
      return this;
   }
}
