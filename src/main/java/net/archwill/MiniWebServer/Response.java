package net.archwill.MiniWebServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.tika.Tika;

public class Response {
  private static final Logger LOGGER = LoggerFactory.getLogger();

  private Request request = new Request();
  private String version = "HTTP/1.0";
  private int statusCode = 200;
  private Map<String, Header> headers = new HashMap<String, Header>();

  public Response() {
    setHeader("Server", "MiniWebServer/0.0.1");
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
    if (statusCode >= 100 && statusCode <= 599) this.statusCode = statusCode;
  }

  public Header getHeader(String key) {
    return headers.get(WordUtils.capitalizeFully(key, '-'));
  }

  public void setHeader(Header header) {
    if (!StringUtils.isBlank(header.getValue())) {
      if (header.getKey().equals("Set-Cookie")) {
        if (!headers.containsKey("Set-Cookie")) headers.put("Set-Cookie", new MultiHeader("Set-Cookie"));
        headers.get("Set-Cookie").setValue(header.getValue());
      } else {
        headers.put(header.getKey(), header);
      }
    }
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
    headers.remove(WordUtils.capitalizeFully(key, '-'));
  }

  public Map<String, Header> getHeaders() {
    return headers;
  }

  @Override
  public String toString() {
    return getVersion() + " " + getStatusCode() + " " + Utils.getStatusText(getStatusCode());
  }

  public void sendHeaders(OutputStream os) throws IOException {
    setHeader("Date", Utils.formatDate(new Date()));
    BufferedOutputStream out = new BufferedOutputStream(os);
    out.write((this.toString() + "\r\n").getBytes());
    for (Header header : headers.values()) {
      out.write((header.toString() + "\r\n").getBytes());
    }
    out.write("\r\n".getBytes());
    out.flush();
    LOGGER.info(getRequest().getMethod() + " " + Integer.toString(getStatusCode()) + " " + getRequest().getUri().toString());
  }

  private void sendStream(OutputStream os, InputStream is) throws IOException {
    int length;
    byte[] buffer = new byte[1024*8];
    if (getRequest().getHeader("Accept-Encoding") != null && getHeader("Content-Encoding") == null) {
      String encodings = getRequest().getHeader("Accept-Encoding").getValue();
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
    setHeader("Last-Modified", Utils.formatDate(new Date(file.lastModified())));
    if (getRequest().getHeader("If-Modified-Since") != null) {
      Date since = Utils.parseDate(getRequest().getHeader("If-Modified-Since").getValue());
      if (since != null && since.getTime() >= file.lastModified()) {
        setStatusCode(304);
        sendHeaders(os);
        return;
      }
    }
    setHeader("Content-Type", new Tika().detect(file));
    setHeader("Content-Length", Long.toString(file.length()));
    if (getRequest().getMethod().equals("HEAD")) {
      sendHeaders(os);
    } else {//if (getRequest().getMethod().equals("GET")) {
      sendStream(os, new BufferedInputStream(new FileInputStream(file)));
    }
  }

  public void sendError(OutputStream os) throws IOException {
    String errorPage = "<!DOCTYPE html><html><head><title>" +
      getStatusCode() + " " + Utils.getStatusText(getStatusCode()) +
      "</title></head><body><h1>" +
      getStatusCode() + " " + Utils.getStatusText(getStatusCode()) +
      "</h1></body></html>";

    setHeader("Content-Type", "text/html");
    send(os, errorPage.getBytes());
  }

  public void sendError(OutputStream os, int statusCode) throws IOException {
    setStatusCode(statusCode);
    sendError(os);
  }

  public void executePHPRedneckStyle(Socket client, File file) throws IOException {
    LOGGER.warning("Executing PHP in a very redneck fashion");

    ProcessBuilder pb = new ProcessBuilder("/usr/bin/php-cgi", "-d", "cgi.force_redirect=0");
    pb.redirectErrorStream(true);
    pb.environment().put("GATEWAY_INTERFACE", "CGI/1.1");
    for (Header header : request.getHeaders().values()) {
      pb.environment().put("HTTP_" + header.getKey().toUpperCase().replace('-', '_'), header.getValue());
    }
    if (getHeader("Host") != null) {
      String[] parts = getHeader("Host").getValue().split(":", 2);
      pb.environment().put("SERVER_NAME", parts[0]);
      if (parts.length == 2) {
        pb.environment().put("SERVER_PORT", parts[1]);
      }
    } else {
      pb.environment().put("HTTP_HOST", "localhost");
      pb.environment().put("SERVER_NAME", "localhost");
      pb.environment().put("SERVER_PORT", "8080");
    }
    if (getRequest().getHeader("Content-Type") != null) pb.environment().put("CONTENT_TYPE", getRequest().getHeader("Content-Type").getValue());
    if (getRequest().getBody() != null) pb.environment().put("CONTENT_LENGTH", Integer.toString(getRequest().getBody().length));
    if (getRequest().getUri().getRawQuery() != null) pb.environment().put("QUERY_STRING", getRequest().getUri().getRawQuery());
    if (getRequest().getHeader("X-Real-Ip") != null) pb.environment().put("REMOTE_ADDR", getRequest().getHeader("X-Real-Ip").getValue());
    else pb.environment().put("REMOTE_ADDR", client.getInetAddress().getHostAddress());
    pb.environment().put("REQUEST_METHOD", getRequest().getMethod());
    pb.environment().put("REQUEST_URI", getRequest().getUri().toString());
    pb.environment().put("SCRIPT_FILENAME", file.getAbsolutePath());
    pb.environment().put("SCRIPT_NAME", getRequest().getUri().getPath());
    pb.environment().put("SERVER_ADDR", "127.0.0.1");
    pb.environment().put("SERVER_ADMIN", "nobody@exemple.com");
    pb.environment().put("SERVER_NAME", "localhost");
    pb.environment().put("SERVER_PROTOCOL", getVersion());
    Process proc = pb.start();
    BufferedInputStream in = new BufferedInputStream(proc.getInputStream());
    if (getRequest().getBody() != null) {
      BufferedOutputStream out = new BufferedOutputStream(proc.getOutputStream());
      out.write(getRequest().getBody());
      out.flush();
      out.close();
    }

    String line;
    while (!StringUtils.isBlank(line = Utils.readLine(in))) setHeader(line);

    int length = 0;
    byte[] buffer = new byte[1024*8];
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((length = in.read(buffer)) > 0) bos.write(buffer, 0, length);

    if (getHeader("PHP Warning") != null) {
      LOGGER.warning(getHeader("PHP Warning").getValue());
      removeHeader("PHP Warning");
    }
    if (getHeader("Status") != null) {
      setStatusCode(Integer.parseInt(getHeader("Status").getValue().substring(0, 2)));
      removeHeader("Status");
    }
    if (getHeader("Location") != null) {
      if (getStatusCode() < 300 || getStatusCode() > 399) setStatusCode(301);
      sendHeaders(client.getOutputStream());
    } else {
      setHeader("Last-Modified", Utils.formatDate(new Date()));
      setHeader("Content-Length", Long.toString(bos.toByteArray().length));
      sendStream(client.getOutputStream(), new ByteArrayInputStream(bos.toByteArray()));
    }
  }
}
