package com.willisite.MiniWebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

class InvalidRequestException extends RuntimeException {
  public InvalidRequestException(String message) {
    super(message);
  }
}

public class Request {
  private static final Logger LOGGER = LoggerFactory.getLogger("Request");

  private String method = "GET";
  private String uri = "/";
  private String version = "HTTP/1.1";
  private HashMap<String, Header> headers = new HashMap<String, Header>();

  public Request() {}

  public Request(String uri) {
    setUri(uri);
  }

  public Request(String method, String uri) {
    this(uri);
    setMethod(method);
  }

  public Request(InputStream is) throws IOException {
    read(is);
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    // TODO: Validate method
    this.method = method.toUpperCase();
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    // TODO: Validate version
    this.version = version;
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
    return method + " " + uri + " " + version;
  }

  public void parseRequest(String request) throws InvalidRequestException {
    String[] parts = request.trim().split("\\s+");
    if (parts.length != 3) throw new InvalidRequestException("Invalid request line format");
    setMethod(parts[0]);
    setUri(parts[1]);
    setVersion(parts[2]);
  }

  public void read(InputStream is) throws InvalidRequestException, IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(is));
    String line = in.readLine();
    parseRequest(line);
    while (!StringUtils.isBlank(line = in.readLine())) setHeader(line);
    LOGGER.info(this.toString());
  }

  public Response createResponse() {
    Response response = new Response();
    response.setVersion(getVersion());
    if (getHeader("Host") != null) response.setHeader(getHeader("Host"));
    return response;
  }
}
