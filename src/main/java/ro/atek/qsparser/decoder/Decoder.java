package ro.atek.qsparser.decoder;

import java.nio.charset.Charset;

/**
 * Special decoder which can be used for any string parsed from the query string.
 */
public interface Decoder
{
   /**
    * The core method of decoding parsed strings.
    *
    * @param  content
    *         The content extracted after parsing and requires decoding.
    * @param  charset
    *         The charset used for decoding.
    * @param  type
    *         The type of content. This can be either the parsed key or value.
    *
    * @return  A decoded string for the provided content.
    */
   String decode(String content, Charset charset, ContentType type);

   /**
    * An internal enum to type check the type of content used for decoding.
    */
   enum ContentType
   {
      /** The key of a query string entry */
      KEY,
      /** The value of a query string entry */
      VALUE
   }
}
