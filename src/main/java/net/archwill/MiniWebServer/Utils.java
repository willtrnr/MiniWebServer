package net.archwill.MiniWebServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.httpclient.HttpStatus;

public class Utils {
  private static final String FORMAT_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
  private static final String FORMAT_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
  private static final String FORMAT_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

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
    SimpleDateFormat format = new SimpleDateFormat(FORMAT_RFC1123, Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    return format.format(date);
  }

  public static Date parseDate(String date) {
    try {
      SimpleDateFormat format = new SimpleDateFormat(FORMAT_RFC1123, Locale.US);
      return format.parse(date);
    } catch (ParseException e) {
    } catch (NumberFormatException e) {}
    try {
      SimpleDateFormat format = new SimpleDateFormat(FORMAT_RFC1036, Locale.US);
      return format.parse(date);
    } catch (ParseException e) {
    } catch (NumberFormatException e) {}
    try {
      SimpleDateFormat format = new SimpleDateFormat(FORMAT_ASCTIME, Locale.US);
      format.setTimeZone(TimeZone.getTimeZone("GMT"));
      return format.parse(date);
    } catch (ParseException e) {
    } catch (NumberFormatException e) {}
    return null;
  }
}
