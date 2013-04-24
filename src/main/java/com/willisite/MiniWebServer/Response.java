package com.willisite.MiniWebServer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.tika.Tika;

public class Response {
  private static final Logger LOGGER = LoggerFactory.getLogger("Response");

  private Request request = new Request();
  private String version = "HTTP/1.0";
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

  public Response(Request request) {
    this();
    setRequest(request);
  }

  public Request getRequest() {
    return request;
  }

  public void setRequest(Request request) {
    setVersion(request.getVersion());
    if (request.getHeader("Host") != null) setHeader(request.getHeader("Host"));
    if (request.getHeader("Connection") != null) setHeader(request.getHeader("Connection"));
    this.request = request;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    version = version.toUpperCase();
    if (version.equals("HTTP/1.0") || version.equals("HTTP/1.1")) this.version = version;
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
    return headers.get(WordUtils.capitalizeFully(key, '-'));
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

  public void removeHeader(String key) {
    headers.remove(key);
  }

  @Override
  public String toString() {
    return getVersion() + " " + getStatusCode() + " " + ((getStatusCode() == 418) ? "I'm a teapot" : HttpStatus.getStatusText(getStatusCode()));
  }

  public void sendHeaders(OutputStream os) throws IOException {
    setHeader("Date", Utils.RFC1123DATEFORMAT.format(new Date()));
    BufferedOutputStream out = new BufferedOutputStream(os);
    out.write(new String(this.toString() + "\r\n").getBytes());
    for (Header header : headers.values()) {
      out.write(new String(header.toString() + "\r\n").getBytes());
    }
    out.write("\r\n".getBytes());
    out.flush();
    LOGGER.info(getRequest().getMethod() + " " + Integer.toString(getStatusCode()) + " " + getRequest().getUri().toString());
  }

  private void sendStream(OutputStream os, InputStream is) throws IOException {
    int length;
    byte[] buffer = new byte[1024*8];
    if (request.getHeader("Accept-Encoding") != null) {
      String encodings = request.getHeader("Accept-Encoding").getValue();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      if (encodings.contains("gzip")) {
        setHeader("Content-Encoding", "gzip");
        GZIPOutputStream out = new GZIPOutputStream(bos);
        while ((length = is.read(buffer)) > 0) out.write(buffer, 0, length);
        out.finish();
        out.flush();
      } else if (encodings.contains("deflate")) {
        setHeader("Content-Encoding", "deflate");
        DeflaterOutputStream out = new DeflaterOutputStream(bos);
        while ((length = is.read(buffer)) > 0) out.write(buffer, 0, length);
        out.finish();
        out.flush();
      }
      setHeader("Content-Length", Integer.toString(bos.toByteArray().length));
      ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());

      sendHeaders(os);
      BufferedOutputStream out = new BufferedOutputStream(os);
      while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
      out.flush();
    } else {
      sendHeaders(os);
      BufferedOutputStream out = new BufferedOutputStream(os);
      while ((length = is.read(buffer)) > 0) out.write(buffer, 0, length);
      out.flush();
    }
  }

  public void send(OutputStream os, byte[] data) throws IOException {
    setHeader("Content-Length", Integer.toString(data.length));
    if (getRequest().getMethod().equals("HEAD")) {
      sendHeaders(os);
    } else {
      sendStream(os, new ByteArrayInputStream(data));
    }
  }

  public void send(OutputStream os, File file) throws IOException {
    setHeader("Last-Modified", Utils.RFC1123DATEFORMAT.format(new Date(file.lastModified())));
    if (request.getHeader("If-Modified-Since") != null) {
      try {
        Date since = Utils.RFC1123DATEFORMAT.parse(request.getHeader("If-Modified-Since").getValue());
        if (since.getTime() >= file.lastModified()) {
          setStatusCode(304);
          sendHeaders(os);
          return;
        }
      } catch (ParseException e) {
      } catch (NumberFormatException e) {}
    }
    setHeader("Content-Type", new Tika().detect(file));
    setHeader("Content-Length", Long.toString(file.length()));
    if (getRequest().getMethod().equals("HEAD")) {
      sendHeaders(os);
    } else {//if (getRequest().getMethod().equals("GET")) {
      sendStream(os, new FileInputStream(file));
    }
  }
}
