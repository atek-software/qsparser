package ro.atek.qsparser.net;

import java.nio.charset.Charset;

/**
 * Special encoder which can be used for any string to be added to the query string.
 */
public interface Encoder
{
   /**
    * The core method of encoding values into a query string.
    *
    * @param  content
    *         The content extracted after parsing and requires encoding.
    * @param  charset
    *         The charset used for encoding.
    * @param  type
    *         The type of content. This can be either the parsed key or value.
    *
    * @return  A encoded string for the provided content.
    */
   String encode(String content, Charset charset, ContentType type);
}
