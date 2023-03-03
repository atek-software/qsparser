package ro.atek.qsparser.value;

import java.util.LinkedHashMap;
import java.util.Map;

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
   public Value merge(Value value)
   {
      if (value instanceof ArrayValue)
      {
         return merge(((ArrayValue) value).asDictValue());
      }

      if (!(value instanceof DictValue))
      {
         throw new RuntimeException("Not implemented yet!");
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

   @Override
   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (!(obj instanceof DictValue)) return false;
      DictValue other = (DictValue) obj;
      if (other.size() != this.size()) return false;
      for (Map.Entry<DictKey, Value> entry : this.entrySet())
      {
         if (!other.containsKey(entry.getKey())) return false;
         if (entry.getValue() == null || other.get(entry.getKey()) == null)
         {
            if (entry.getValue() != other.get(entry.getKey())) return false;
            continue;
         }
         if (!entry.getValue().equals(other.get(entry.getKey()))) return false;
      }
      return true;
   }

   /**
    * Convenient representation of this value.
    *
    * @return  A verbose and indented string representation of a dictionary.
    */
   @Override
   public String toString()
   {
      return toString(0);
   }

   /**
    * The ident aware stringify method. The ident is of 3 spaces.
    *
    * @param   ident
    *          The ident to which this should be aligned.
    *
    * @return  An indented string representation of a dictionary
    */
   public String toString(int ident)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("{\n");
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
            sb.append("NaN\n");
            continue;
         }
         if (entry.getValue() instanceof DictValue)
         {
            // make it aware of ident
            sb.append(((DictValue) entry.getValue()).toString(ident));
         }
         else
         {
            // !! the very nested dictionaries are not aware of the ident
            sb.append(entry.getValue());
            sb.append("\n");
         }
      }
      ident -= 3;
      for (int i = 0; i < Math.max(0, ident); i++)
      {
         sb.append(" ");
      }
      sb.append("}\n");
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
      put(key, value);
      return this;
   }
}
