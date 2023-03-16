package ro.atek.qsparser.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class DefaultEncoder
implements Encoder
{
   /** Convenient singleton */
   public static final DefaultEncoder INSTANCE = new DefaultEncoder();

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
