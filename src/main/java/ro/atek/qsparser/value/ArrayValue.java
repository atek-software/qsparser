package ro.atek.qsparser.value;

import java.util.*;
import java.util.stream.Stream;

/**
 * Compound value type which allows representing data in an array
 * format. This type of value is immutable as the value interface
 * requires.
 */
public final class ArrayValue
implements Value
{
   /** The underlying data of the array */
   private final Value[] values;

   /**
    * Wrapper constructor
    *
    * @param  values
    *         The values to be wrapped by thia array value.
    */
   public ArrayValue(Value[] values)
   {
      this.values = values;
   }

   /**
    * Convenient constructor which can combine two values into an array.
    * The array operands are not appended, but their values are appended.
    * Therefore, this value is the concatenation of the two values. If an
    * operand is not an array, make a singleton array out of it.
    *
    * @param   a
    *          The first operand to be concatenated in this array.
    * @param   b
    *          The second operand to be concatenated in this array.
    */
   public ArrayValue(Value a, Value b)
   {
      Value[] val1 = a instanceof ArrayValue ? ((ArrayValue) a).values : new Value[] { a };
      Value[] val2 = b instanceof ArrayValue ? ((ArrayValue) b).values : new Value[] { b };
      values = Stream.concat(Arrays.stream(val1), Arrays.stream(val2)).toArray(Value[]::new);
   }

   /**
    * Special constructor for an array with a single value on a specified position.
    * Note that the other positions are null, not {@link NullValue}.
    *
    * @param  index
    *         The index where the value should be set.
    * @param  value
    *         The value to be set on the specified position.
    */
   public ArrayValue(int index, Value value)
   {
      this.values = new Value[index + 1];
      this.values[index] = value;
   }

   /**
    * Implementation of the merge routine. This allows merging this array
    * with any other arbitrary value. The result is a new instance, so neither
    * this nor the operand is altered.
    * <p>
    * This can only merge with a string value (concatenating it at then end),
    * a dictionary (using the dictionary representation of this array) or another
    * array.
    * <p>
    * In case another array is merged in this, don't simply concatenate. Usually,
    * arrays have only some values set on some specific indexes. This means
    * merging the values at each index, if exists.
    * <p>
    * There are special cases where trying to merge incompatible values. In this
    * case, append the value at the end.
    *
    * @param   value
    *          Another value to be merged into this.
    *
    * @return  The merged result.
    */
   @Override
   public Value merge(Value value)
   {
      if (value instanceof StringValue)
      {
         return new ArrayValue(this, value);
      }

      if (value instanceof DictValue)
      {
         return asDictValue().merge(value);
      }

      if (!(value instanceof ArrayValue))
      {
         throw new RuntimeException("Not implemented yet!");
      }

      ArrayValue other = (ArrayValue) value;
      Value[] resultValues = new Value[Math.max(getSize(), other.getSize())];
      System.arraycopy(this.values, 0, resultValues, 0, this.values.length);

      DictValue values = other.asDictValue();
      List<Value> extra = new ArrayList<>();
      for (Map.Entry<DictKey, Value> elem : values.entrySet())
      {
         int key = ((IntValue) elem.getKey()).intern();
         if (resultValues[key] == null)
         {
            resultValues[key] = elem.getValue();
         }
         else if (elem.getValue() instanceof DictValue && resultValues[key] instanceof DictValue)
         {
            resultValues[key] = resultValues[key].merge(elem.getValue());
         }
         else
         {
            extra.add(elem.getValue());
         }
      }

      Value[] oldValues = resultValues;
      resultValues = new Value[oldValues.length + extra.size()];
      System.arraycopy(oldValues, 0, resultValues, 0, oldValues.length);
      for (int i = oldValues.length; i < resultValues.length; i++)
      {
         resultValues[i] = extra.get(i - oldValues.length);
      }

      return new ArrayValue(resultValues);
   }

   /**
    * Retrieve the dictionary representation of this array. This means a
    * dictionary in which the key is the index and the value is the value
    * at the respective index from the array. Note {@code null} values are not
    * represented in the final dictionary.
    *
    * @return  A dictionary representation of this array with indexes as keys.
    */
   public DictValue asDictValue()
   {
      DictValue dict = new DictValue();
      for (int i = 0; i < values.length; i++)
      {
         if (values[i] == null) continue;
         dict.put(IntValue.get(i), values[i]);
      }
      return dict;
   }

   /**
    * Retrieve the number of elements from this array.
    */
   public int getSize()
   {
      return values.length;
   }

   /**
    * Retrieve the array underlying this wrapper. In fact, the result
    * is a copy to keep safe from mutability.
    *
    * @return   A clone of the underlying array.
    */
   public Value[] intern()
   {
      return values.clone();
   }

   /**
    * Implementation of the compact method. This is the real use-case for
    * compact. It basically removes all null values from the array and
    * shifts all values to form a contiguous segment at the beginning.
    *
    * @return   An array value which is compacted, so there is no {@code null}
    *           between values.
    */
   @Override
   public ArrayValue compact()
   {
      List<Value> target = new ArrayList<>();
      for (Value val : values)
      {
         if (val != null)
         {
            target.add(val.compact());
         }
      }
      return new ArrayValue(target.toArray(new Value[0]));
   }

   /**
    * Convenient representation of this value.
    *
    * @return  Simply use the Java codification of arrays.
    */
   @Override
   public String toString()
   {
      return Arrays.toString(values);
   }

   /**
    * Implementation of the equal check. This should return {@code true}
    * only if the other object is an {@link ArrayValue} and wraps the
    * exact values (non-compact).
    *
    * @param    other
    *           The other object to be used for the equal check.
    *
    * @return   {@code true} only when the other value is an {@code ArrayValue}
    *           and wraps a similar array.
    */
   @Override
   public boolean equals(Object other)
   {
      if (this == other) return true;
      if (!(other instanceof ArrayValue)) return false;
      ArrayValue otherArray = (ArrayValue) other;
      return Arrays.equals(values, otherArray.values);
   }
}
