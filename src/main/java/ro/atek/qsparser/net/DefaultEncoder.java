package ro.atek.qsparser.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * The default encoder which uses the standard {@link URLEncoder}. If one
 * needs a more complex encoding procedure, it can extend this and do extra
 * processing steps.
 */
public class DefaultEncoder
implements Encoder
{
   /** Convenient singleton */
   public static final DefaultEncoder INSTANCE = new DefaultEncoder();

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
   @Override
   public String encode(String content, Charset charset, ContentType type)
   {
      if (content == null)
      {
         return null;
      }
      try
      {
         return URLEncoder.encode(content, charset.name());
      }
      catch (IllegalArgumentException | UnsupportedEncodingException e)
      {
         return content;
      }
   }
}
