package com.willisite.MiniWebServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public final class Utils {
  public static final SimpleDateFormat RFC1123DATEFORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

  public static String readLine(InputStream is) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    int b = 0;
    while ((b = is.read()) != '\n')
      if (b == -1) break;
      else os.write(b);
    if (os.size() == 0) return null;
    return new String(os.toByteArray());
  }
}
