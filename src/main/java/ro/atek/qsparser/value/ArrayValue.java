package ro.atek.qsparser.value;

import ro.atek.qsparser.ArrayFormat;
import ro.atek.qsparser.StringifyOptions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Compound value type which allows representing data in an array
 * format. This type of value is immutable as the value interface
 * requires.
 */
public class ArrayValue
extends ArrayList<Value>
implements Value
{
   /**
    * Wrapper constructor
    *
    * @param  values
    *         The values to be wrapped by thia array value.
    */
   public ArrayValue(List<Value> values)
   {
      super(values);
   }

   /**
    * Wrapper constructor
    *
    * @param  values
    *         The values to be wrapped by thia array value.
    */
   public ArrayValue(Value[] values)
   {
      Collections.addAll(this, values);
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
      a = a == null ? NullValue.get() : a;
      b = b == null ? NullValue.get() : b;
      Value[] val1 = a.getType() == ValueType.ARRAY ? ((ArrayValue) a).toArray(new Value[0]) : new Value[] { a };
      Value[] val2 = b.getType() == ValueType.ARRAY ? ((ArrayValue) b).toArray(new Value[0]) : new Value[] { b };
      this.addAll(Stream.concat(Arrays.stream(val1), Arrays.stream(val2)).collect(Collectors.toList()));
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
      this(new Value[index + 1]);
      this.set(index, value == null ? NullValue.get() : value);
   }

   /**
    * Represent the value as a query string. This is different from
    * {@code toString} in the sense that the result can be parsed
    * back as this value.
    *
    * @param   options
    *          The options used when generating the query string.
    *
    * @return  A query string representation of this value.
    */
   @Override
   public List<QueryStringEntry> stringify(String key, StringifyOptions options)
   {
      if (key == null || options.arrayFormat == ArrayFormat.COMMA)
      {
         List<String> chunks = new ArrayList<>();
         for (int i = 0; i < this.size(); i++)
         {
            Value val = this.get(i);
            if (val == null)
            {
               continue;
            }
            List<String> values = val.stringify(null, options).get(0).getValues();
            if (values != null)
            {
               chunks.addAll(values);
            }
         }

         String newKey = options.commaRoundTrip && this.size() <= 1 ? key + "[]" : key;
         return Collections.singletonList(new QueryStringEntry(newKey, chunks));
      }
      List<QueryStringEntry> strs = new ArrayList<>();
      for (int i = 0; i < this.size(); i++)
      {
         Value val = this.get(i);
         if (val == null)
         {
            continue;
         }
         String newKey = String.valueOf(i);
         if (!key.isEmpty())
         {
            newKey = key;
            if (options.indices)
            {
               switch (options.arrayFormat)
               {
                  case INDICES:
                     newKey = key + "[" + i + "]";
                     break;
                  case BRACKETS:
                     newKey = key + "[]";
                     break;
                  case REPEAT:
                     newKey = key;
                     break;
               }
            }
         }
         strs.addAll(val.stringify(newKey, options));
      }
      return strs;
   }

   /**
    * Implementation of the merge routine. This allows merging this array
    * with any other arbitrary value. The result is a new instance, so neither
    * this nor the operand is altered.
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
      if (value == null)
      {
         return this;
      }

      if (value.getType() == ValueType.DICT)
      {
         return asDictValue().merge(value);
      }

      if (value.getType() != ValueType.ARRAY)
      {
         return new ArrayValue(this, value);
      }

      ArrayValue other = (ArrayValue) value;
      ArrayValue newValues = new ArrayValue(this);

      for (int i = 0; i < other.size(); i++)
      {
         boolean has = i < newValues.size() && newValues.get(i) != null;
         if (has &&
             other.get(i) != null &&
             other.get(i).getType() == ValueType.DICT &&
             newValues.get(i).getType() == ValueType.DICT)
         {
            newValues.set(i, newValues.get(i).merge(other.get(i)));
         }
         else if (has)
         {
            newValues.add(other.get(i));
         }
         else
         {
            newValues.set(i, other.get(i));
         }
      }

      return newValues;
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
      for (int i = 0; i < size(); i++)
      {
         if (get(i) == null) continue;
         dict.put(IntValue.get(i), get(i));
      }
      return dict;
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
      for (Value val : this)
      {
         if (val != null)
         {
            target.add(val.compact());
         }
      }
      return new ArrayValue(target);
   }

   /**
    * Retrieve the value type of this.
    *
    * @return   The array type.
    */
   @Override
   public ValueType getType()
   {
      return ValueType.ARRAY;
   }

   /**
    * Override the super method to automatically expend the array when
    * doing a set. This is widely used in the parsing process as one
    * can set a value to an index which is not yet in array's bounds.
    *
    * @param index index of the element to replace
    * @param value element to be stored at the specified position
    *
    * @return  The element which was previously positioned at index.
    */
   @Override
   public Value set(int index, Value value)
   {
      while (size() <= index) this.add(null);
      return super.set(index, value);
   }

   /**
    * Convenient representation of this value.
    *
    * @return  Simply use the Java codification of arrays.
    */
   @Override
   public String toString()
   {
      return "[" + stream().map(Value::toString).collect(Collectors.joining(",")) + "]";
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
      if (this.size() != otherArray.size()) return false;
      for (int i = 0; i < this.size(); i++)
      {
         if (get(i) == null || otherArray.get(i) == null)
         {
            if (get(i) != otherArray.get(i))
               return false;
            continue;
         }
         if (!get(i).equals(otherArray.get(i)))
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Implementation of the hash code. This is based on the cumulative
    * array elements hashes built-in for Java.
    *
    * @return   The hash code of this array value.
    */
   @Override
   public int hashCode()
   {
      return super.hashCode();
   }
}
