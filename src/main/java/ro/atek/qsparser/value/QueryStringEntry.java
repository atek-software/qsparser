package ro.atek.qsparser.value;

import java.util.Collections;
import java.util.List;

/**
 * Utility class used to store a key value combination. This is a helper
 * representation for when building a query string. This should store a single
 * entry in the query string.
 */
public class QueryStringEntry
{
   /** The key of the entry */
   private final String key;

   /**
    * Usually, this is a singleton, but using commas for arrays should combine the values into a
    * single string. These are segregated to honor the encoding of values, and not the joining comma.
    */
   private final List<String> values;

   /**
    * Convenient wrapper constructor.
    * @param   key
    *          The key of the entry
    * @param   value
    *          The value to which the key is mapped.
    */
   public QueryStringEntry(String key, String value)
   {
      this(key, value == null ? null : Collections.singletonList(value));
   }

   /**
    * Wrapper constructor.
    * @param   key
    *          The key of the entry
    * @param   values
    *          The values to which the key is mapped.
    */
   public QueryStringEntry(String key, List<String> values)
   {
      this.key = key;
      this.values = values;
   }

   /**
    * Getter for {@link #key}.
    *
    * @return   The key of the entry
    */
   public String getKey()
   {
      return key;
   }
   /**
    * Getter for {@link #values}.
    *
    * @return   The values to which the key is mapped.
    */
   public List<String> getValues()
   {
      return values;
   }
}
