package ro.atek.qsparser;

import java.util.Collections;
import java.util.List;

public class QueryStringEntry
{
   private final String key;
   private final List<String> values;

   public QueryStringEntry(String key, String value)
   {
      this.key = key;
      this.values = value == null ? null : Collections.singletonList(value);
   }

   public QueryStringEntry(String key, List<String> values)
   {
      this.key = key;
      this.values = values;
   }

   public String getKey()
   {
      return key;
   }

   public List<String> getValues()
   {
      return values;
   }
}
