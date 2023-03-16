package ro.atek.qsparser.value;

import ro.atek.qsparser.QueryStringEntry;
import ro.atek.qsparser.StringifyOptions;

import java.util.*;

/**
 * Compound value type which allows representing data in a dictionary
 * format. This extends {@link LinkedHashMap} to allow fast look-up.
 * <p>
 * This extends {@link LinkedHashMap}, making it mutable. The invariant
 * of the {@link Value} interface is to keep parsed values immutable. It
 * is highly discouraged to use this as mutable, as one such instance can
 * be nested into other compound values as well.
 */
public final class DictValue
extends LinkedHashMap<DictKey, Value>
implements Value
{
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
      if (key == null)
      {
         return Collections.singletonList(new QueryStringEntry(null, options.dictInArrayReplacement));
      }
      List<QueryStringEntry> values = new ArrayList<>(this.size());
      for (Map.Entry<DictKey, Value> entry : this.entrySet())
      {
         if (options.skipNulls && (entry.getValue() == null || entry.getValue().getType() == ValueType.NULL))
         {
            continue;
         }

         if (key.isEmpty())
         {
            values.addAll(entry.getValue().stringify(entry.getKey().toString(), options));
         }
         else
         {
            String newKey = key + (options.allowDots ? "." + entry.getKey() : "[" + entry.getKey() + "]");
            values.addAll(entry.getValue().stringify(newKey, options));
         }
      }
      return values;
   }

   /**
    * Merge implementation. This is a complex logic,
    * allowing the merging of a dictionary value with any kind
    * of other value. The logic is based on how the query string
    * is commonly serialized.
    * <p>
    * Merging works only with:
    * <ul>
    *    <li>An array, merging with its dictionary representation from
    *    {@link ArrayValue#asDictValue}}</li>
    *    <li>A dictionary, merging the values from the same key.</li>
    * </ul>
    *
    * @param   value
    *          Another value to be merged into this.
    *
    * @return  The result after merging another value into this dictionary.
    */
   @Override
   public Value merge(Value value)
   {
      if (value == null)
      {
         return this;
      }

      if (value.getType() == ValueType.ARRAY)
      {
         return merge(((ArrayValue) value).asDictValue());
      }

      if (value.getType() != ValueType.DICT)
      {
         if (value instanceof DictKey)
         {
            DictValue result = new DictValue();
            result.putAll(this);
            return result.append((DictKey) value, BoolValue.TRUE);
         }
         else
         {
            return new ArrayValue(this, value);
         }
      }

      DictValue other = (DictValue) value;
      DictValue result = new DictValue();
      result.putAll(this);
      for (Map.Entry<DictKey, Value> entry : other.entrySet())
      {
         if (result.containsKey(entry.getKey()))
         {
            Value val = get(entry.getKey());
            result.put(entry.getKey(), val.merge(entry.getValue()));
         }
         else
         {
            result.put(entry.getKey(), entry.getValue());
         }
      }
      return result;
   }

   /**
    * A mean of compacting this value. This basically calls the compacting
    * procedure of each stored value.
    *
    * @return   Another dictionary value storing
    */
   @Override
   public DictValue compact()
   {
      DictValue result = new DictValue();
      result.putAll(this);
      for (Map.Entry<DictKey, Value> entry : result.entrySet())
      {
         result.replace(entry.getKey(), entry.getValue() == null ? null : entry.getValue().compact());
      }
      return result;
   }

   /**
    * Retrieve the value type of this.
    *
    * @return   The dictionary type.
    */
   @Override
   public ValueType getType()
   {
      return ValueType.DICT;
   }

   /**
    * Implementation of the equal check. This should return {@code true}
    * only if the other object is an {@link DictValue} and wraps the
    * same key-value entries as this is the same order.
    *
    * @param    obj
    *           The other object to be used for the equal check.
    *
    * @return   {@code true} only when the other value is an {@link DictValue}
    *           and wraps a similar dictionary with the same key order.
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (!(obj instanceof DictValue)) return false;
      DictValue other = (DictValue) obj;
      if (other.size() != this.size()) return false;
      Iterator<Map.Entry<DictKey, Value>> it1 = this.entrySet().iterator();
      Iterator<Map.Entry<DictKey, Value>> it2 = other.entrySet().iterator();
      while (it1.hasNext() && it2.hasNext())
      {
         Map.Entry<DictKey, Value> a = it1.next();
         Map.Entry<DictKey, Value> b = it2.next();

         if (!a.getKey().equals(b.getKey())) return false;
         if (a.getValue() == null || b.getValue() == null)
         {
            if (a.getValue() != b.getValue()) return false;
            continue;
         }
         if (!a.getValue().equals(b.getValue())) return false;
      }
      return true;
   }

   /**
    * Implementation of the hash code. This is based on the cumulative
    * map elements hashes built-in for Java.
    *
    * @return   The hash code of this dictionary value.
    */
   @Override
   public int hashCode()
   {
      return super.hashCode();
   }

   /**
    * Convenient representation of this value.
    *
    * @return  A verbose and indented string representation of a dictionary.
    */
   @Override
   public String toString()
   {
      return toString(0, false);
   }

   /**
    * The ident aware stringify method. The ident is of 3 spaces.
    *
    * @param   ident
    *          The ident to which this should be aligned.
    *
    * @return  An indented string representation of a dictionary
    */
   public String toString(int ident, boolean inline)
   {
      StringBuilder sb = new StringBuilder();
      String nl = inline ? "" : "\n";
      sb.append("{").append(nl);
      ident += 3;
      for (Map.Entry<DictKey, Value> entry : entrySet())
      {
         for (int i = 0; i < Math.max(0, ident); i++)
         {
            sb.append(" ");
         }
         sb.append(entry.getKey()).append(" : ");
         if (entry.getValue() == null)
         {
            // don't use "null"; the NullValue is represented as "null"
            sb.append("NaN").append(nl);
            continue;
         }
         if (entry.getValue().getType() == ValueType.DICT)
         {
            // make it aware of ident
            sb.append(((DictValue) entry.getValue()).toString(ident, inline));
         }
         else
         {
            // !! the very nested dictionaries are not aware of the ident
            sb.append(entry.getValue());
            sb.append(nl);
         }
      }
      ident -= 3;
      for (int i = 0; i < Math.max(0, ident); i++)
      {
         sb.append(" ");
      }
      sb.append("}").append(nl);
      return sb.toString();
   }

   /**
    * This is a convenient method like {@code put} which also return
    * this instance. This is not meant to be widely used; its duty
    * is to make testing easier without extra builder classes.
    * <p>
    * Note that this is mutable!
    *
    * @param  key
    *         The key of the entry to be appended.
    * @param  value
    *         The value of the entry to be appended.
    *
    * @return This instance with a new entry.
    */
   public DictValue append(DictKey key, Value value)
   {
      put(key, value == null ? NullValue.get() : value);
      return this;
   }
}
