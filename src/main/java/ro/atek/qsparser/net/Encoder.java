package ro.atek.qsparser.net;

import java.nio.charset.Charset;

public interface Encoder
{
   /**
    * The core method of encoding values into a query string.
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
   String encode(String content, Charset charset, ContentType type);
}
