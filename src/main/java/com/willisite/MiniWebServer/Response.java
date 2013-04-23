package com.willisite.MiniWebServer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class Response {
  private static final Logger LOGGER = LoggerFactory.getLogger("Response");

  private String version = "HTTP/1.1";
  private int statusCode = 200;
  private HashMap<String, Header> headers = new HashMap<String, Header>();

  public Response() {
    setHeader("Server", "MiniWebServer");
    setHeader("Connection", "close");
  }

  public Response(int statusCode) {
    this();
    setStatusCode(statusCode);
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    // TODO: Validate version
    this.version = version;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    if (statusCode >= 100 && statusCode <= 599) {
      this.statusCode = statusCode;
    }
  }

  public Header getHeader(String key) {
    return headers.get(WordUtils.capitalizeFully(key));
  }

  public void setHeader(Header header) {
    if (!StringUtils.isBlank(header.getValue())) headers.put(header.getKey(), header);
  }

  public void setHeader(String key, String value) {
    Header h = new Header(key, value);
    setHeader(h);
  }

  public void setHeader(String header) {
    Header h = Header.parseHeader(header);
    setHeader(h);
  }

  @Override
  public String toString() {
    return version + " " + statusCode + " " + ((statusCode == 418) ? "I'm a teapot" : HttpStatus.getStatusText(statusCode));
  }

  public void sendHeaders(OutputStream os) throws IOException {
    setHeader("Date", HttpServer.RFC1123DATEFORMAT.format(new Date()));
    BufferedOutputStream out = new BufferedOutputStream(os);
    out.write(new String(this.toString() + "\r\n").getBytes());
    for (Header header : headers.values()) {
      out.write(new String(header.toString() + "\r\n").getBytes());
    }
    out.write("\r\n".getBytes());
    out.flush();
    LOGGER.info(this.toString());
  }

  public void send(OutputStream os, byte[] data, int len) throws IOException {
    setHeader("Content-Length", Integer.toString(data.length));
    sendHeaders(os);
    BufferedOutputStream out = new BufferedOutputStream(os);
    out.write(data, 0, len);
    out.flush();
  }

  public void send(OutputStream os, byte[] data) throws IOException {
    send(os, data, data.length);
  }

  public void send(OutputStream os, File file) throws IOException {
    setHeader("Content-Length", Long.toString(file.length()));
    setHeader("Content-Type", "application/octet-stream");
    try {
      MagicMatch match = new Magic().getMagicMatch(file, true, true);
      setHeader("Content-Type", match.getMimeType());
    } catch (MagicParseException e) {
    } catch (MagicMatchNotFoundException e) {
    } catch (MagicException e) {}
    sendHeaders(os);

    BufferedOutputStream out = new BufferedOutputStream(os);
    FileInputStream in = new FileInputStream(file);
    int length;
    byte[] buffer = new byte[1024*8];
    while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
    out.flush();
  }
}
