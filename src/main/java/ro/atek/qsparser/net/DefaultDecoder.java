package ro.atek.qsparser.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * The default decoder which uses the standard {@code URLDecoder}. If one
 * needs a more complex decoding procedure, it can extend this and do extra
 * processing steps.
 */
public class DefaultDecoder
implements Decoder
{
   /** Convenient singleton */
   public static final DefaultDecoder INSTANCE = new DefaultDecoder();

   /**
    * Implicit constructor. Make sure you use {@link #INSTANCE} if
    * you are going to use the default decoder anyway.
    */
   public DefaultDecoder()
   {

   }

   /**
    * The core method of decoding parsed strings. This uses {@code URLDecoder}.
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
   @Override
   public String decode(String content, Charset charset, ContentType type)
   {
      if (content == null)
      {
         return null;
      }
      content = content.replace("+", " ");
      try
      {
         return URLDecoder.decode(content, charset.name());
      }
      catch (IllegalArgumentException | UnsupportedEncodingException e)
      {
         return content;
      }
   }
}
