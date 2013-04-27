package com.willisite.MiniWebServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.httpclient.HttpStatus;

public final class Utils {
  private static final SimpleDateFormat FORMAT_ASCTIME = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
  private static final SimpleDateFormat FORMAT_RFC1036 = new SimpleDateFormat("EEE, dd-MMM-yy HH:mm:ss zzz");
  private static final SimpleDateFormat FORMAT_RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

  public static String readLine(InputStream is) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    int b = 0;
    while ((b = is.read()) != '\n')
      if (b == -1) break;
      else os.write(b);
    if (os.size() == 0) return null;
    return new String(os.toByteArray());
  }

  public static String getStatusText(int statusCode) {
    return ((statusCode == 418) ? "I'm a teapot" : HttpStatus.getStatusText(statusCode));
  }

  public static String formatDate(Date date) {
    FORMAT_RFC1123.setTimeZone(TimeZone.getTimeZone("GMT"));
    return FORMAT_RFC1123.format(date);
  }

  public static Date parseDate(String date) {
    try {
      return FORMAT_RFC1123.parse(date);
    } catch (ParseException e) {
    } catch (NumberFormatException e) {}
    try {
      return FORMAT_RFC1036.parse(date);
    } catch (ParseException e) {
    } catch (NumberFormatException e) {}
    try {
      return FORMAT_ASCTIME.parse(date);
    } catch (ParseException e) {
    } catch (NumberFormatException e) {}
    return null;
  }
}
