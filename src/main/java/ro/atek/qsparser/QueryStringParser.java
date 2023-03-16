package ro.atek.qsparser;

import ro.atek.qsparser.net.*;
import ro.atek.qsparser.value.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Core parser of the query strings.
 */
public class QueryStringParser
{
   /** The configuration used by this parser */
   private final ParserOptions options;

   /**
    * Basic constructor. This starts with the default configuration.
    */
   public QueryStringParser()
   {
      this(null);
   }

   /**
    * Constructor based on a specified configuration. Don't use this
    * if you are going to use the default settings anyway.
    *
    * @param   options
    *          The configuration to be used by this parser. {@code null}
    *          means use the default settings.
    */
   public QueryStringParser(ParserOptions options)
   {
      this.options = options == null ? ParserOptions.DEFAULT : options;
   }

   /**
    * Core procedure which parses a string representing a query string.
    *
    * @param  content
    *         A string which is expected to be a query string
    *
    * @return An internal representation of the query string.
    */
   public Value parse(String content)
   {
      if (content == null || content.isEmpty())
      {
         return new DictValue();
      }

      DictValue tmpObj = parseValues(content);
      Value obj = new DictValue();
      for (Map.Entry<DictKey, Value> entry : tmpObj.entrySet())
      {
         Value newObj = parseKeys(entry.getKey(), entry.getValue());
         obj = obj.merge(newObj);
      }

      if (options.allowSparse)
      {
         return obj;
      }

      return obj.compact();
   }

   /**
    * Internal parser for the key part. This is complex as it con parse into
    * a nested succession of dictionaries and arrays. This is also sensitive
    * to the configuration and its depth and array limits.
    *
    * @param   dictKey
    *          The key to be parsed.
    * @param   value
    *          The value to be assigned to the specified key.
    *
    * @return  Usually a dictionary with a single key pointing to the specified value.
    */
   private Value parseKeys(DictKey dictKey, Value value)
   {
      String key = dictKey.toString();
      if (options.allowDots)
      {
         key = rewriteDots(key);
      }

      int idx = options.depth == 0 ? -1 : key.indexOf("[");
      String parent = idx != -1 ? key.substring(0, idx) : key;
      String rest = idx != -1 ? key.substring(idx) : null;

      List<String> keys = new ArrayList<>();
      if (parent != null && !parent.isEmpty())
      {
         keys.add(parent);
      }

      for (int i = 0; rest != null && rest.startsWith("[") && rest.contains("]") && i < options.depth; i++)
      {
         int closeIdx = rest.indexOf("]");
         String segment = rest.substring(0, closeIdx + 1);
         keys.add(segment);
         rest = rest.substring(closeIdx + 1);
      }

      if (rest != null && !rest.isEmpty())
      {
         keys.add("[" + rest + "]");
      }

      return parseObject(keys, value);
   }

   /**
    * Internal helper for the key parser. This takes the split key and
    * tries to build the nested structure. This doesn't honor the depth
    * or comma settings.
    *
    * @param   chain
    *          A list of identifiers obtained by splitting the key.
    * @param   value
    *          The value to which the final key should be assigned.
    *
    * @return  Usually a nested dictionary with the provided keys in the chain
    *          pointing to the provided value.
    */
   private Value parseObject(List<String> chain, Value value)
   {
      Value leaf = value;
      for (int i = chain.size() - 1; i >= 0; i--)
      {
         String root = chain.get(i);
         if (root.equals("[]") && options.parseArrays)
         {
            leaf = leaf.getType() == ValueType.ARRAY ? leaf : new ArrayValue(new Value[] { leaf });
            continue;
         }

         Value obj;
         String cleanRoot = root.startsWith("[") && root.endsWith("]") ?
            root.substring(1, root.length() - 1) :
            root;
         boolean failIndex = false;
         int index = -1;
         try
         {
            index = Integer.parseInt(cleanRoot);
         }
         catch (NumberFormatException ignored)
         {
            failIndex = true;
         }
         if (!options.parseArrays && cleanRoot.isEmpty())
         {
            obj = new DictValue().append(IntValue.get(0), leaf);
         }
         else if (!failIndex &&
                  !root.equals(cleanRoot) &&
                  String.valueOf(index).equals(cleanRoot) &&
                  index >= 0 &&
                  (options.parseArrays && index <= options.arrayLimit))
         {
            obj = new ArrayValue(index, leaf);
         }
         else if (options.parseIntKeys && !failIndex && String.valueOf(index).equals(cleanRoot))
         {
            obj = new DictValue().append(IntValue.get(index), leaf);
         }
         else
         {
            obj = new DictValue().append(StringValue.get(cleanRoot), leaf);
         }
         leaf = obj;
      }

      return leaf;
   }

   /**
    * Internal procedure which handles the parsing of the values.
    * This is does the whole initial heavy-lift as it splits the query string
    * and tries to identify the entries, honoring any additional markers and decoding.
    *
    * @param   content
    *          A string representing the query string.
    *
    * @return  A structured representation of the query string.
    */
   private DictValue parseValues(String content)
   {
      content = options.ignoreQueryPrefix && content.startsWith("?") ?
         content.substring(1) :
         content;
      String[] parts = content.split(options.delimiter);
      if (parts.length > options.parameterLimit)
      {
         String[] oldParts = parts;
         parts = new String[options.parameterLimit];
         System.arraycopy(oldParts, 0, parts, 0, options.parameterLimit);
      }

      Charset charset = options.charset;
      Decoder decoder = options.decoder;
      if (options.charsetSentinel)
      {
         Charset newCharset = findCharsetSentinel(parts);
         if (newCharset != null)
         {
            charset = newCharset;
         }
      }

      DictValue root = new DictValue();
      for (String part : parts)
      {
         if (isCharsetSentinel(part) || part.trim().isEmpty())
         {
            continue;
         }

         int bracketEqualsPos = part.indexOf("]=");
         int pos = bracketEqualsPos == -1 ? part.indexOf('=') : bracketEqualsPos + 1;

         String key;
         Value val;
         if (pos == -1)
         {
            key = decoder.decode(part, charset, ContentType.KEY);
            val = options.strictNullHandling ? NullValue.get() : StringValue.get("");
         }
         else
         {
            key = decoder.decode(part.substring(0, pos), charset, ContentType.KEY);

            String plain = part.substring(pos + 1);
            if (options.comma && plain.contains(","))
            {
               String[] tokens = plain.split(",");
               StringValue[] newTokens = new StringValue[tokens.length];
               for (int j = 0; j < tokens.length; j++)
               {
                  newTokens[j] = StringValue.get(decoder.decode(tokens[j], charset, ContentType.VALUE));
               }
               val = new ArrayValue(newTokens);
            }
            else
            {
               val = StringValue.get(decoder.decode(plain, charset, ContentType.VALUE));
            }
         }

         if (val != null && options.interpretNumericEntities && charset == StandardCharsets.ISO_8859_1)
         {
            val = val.interpretAsNumeric();
         }

         StringValue strKey = StringValue.get(key);
         if (root.containsKey(strKey))
         {
            root.put(strKey, new ArrayValue(root.get(strKey), val));
         }
         else
         {
            root.put(strKey, val);
         }
      }

      return root;
   }

   /**
    * Internal procedure meant to identify a specified charset from
    * encoded sentinel inside query string.
    *
    * @param   parts
    *          The parts after splitting the query string.
    *
    * @return  An identified charset from an eventual sentinel or {@code null} if none exist.
    */
   private Charset findCharsetSentinel(String[] parts)
   {
      for (String part : parts)
      {
         if (isCharsetSentinel(part))
         {
            if (part.equals("utf8=%E2%9C%93"))
            {
               return StandardCharsets.UTF_8;
            }
            else if (part.equals("utf8=%26%2310003%3B"))
            {
               return StandardCharsets.ISO_8859_1;
            }
         }
      }
      return null;
   }

   /**
    * Check if the provided query string part is a charset sentinel.
    * This can specify the charset to be used when decoding the query string.
    *
    * @param   part
    *          The part to be checked as charset sentinel.
    *
    * @return  {@code true} if the part is a charset sentinel.
    */
   private boolean isCharsetSentinel(String part)
   {
      return part.startsWith("utf8=");
   }

   /**
    * Transformer procedure which normalizes the key representation. The key
    * can contain dots and square bracket. The result of this is a normalized
    * form in which only square brackets are used. Therefore, each member specified
    * with a dot is wrapped with square brackets.
    *
    * @param  key
    *         The key whose dots should be rewritten in square brackets.
    *
    * @return A transformed key containing only square brackets.
    */
   private String rewriteDots(String key)
   {
      while (key.contains("."))
      {
         int idx = key.indexOf(".");
         String left = key.substring(0, idx);
         String right = key.substring(idx + 1);

         int idx1 = right.indexOf("[");
         int idx2 = right.indexOf(".");
         if (idx1 == -1 && idx2 == -1)
         {
            key = left + "[" + right + "]";
         }
         else
         {
            int till;
            if (idx1 == -1) till = idx2;
            else if (idx2 == -1) till = idx1;
            else till = Math.min(idx1, idx2);

            String prop = right.substring(0, till);
            String rest = right.substring(till);
            key = left + "[" + prop + "]" + rest;
         }
      }

      return key;
   }
}
