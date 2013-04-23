package com.willisite.MiniWebServer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

class InvalidHeaderException extends RuntimeException {
  public InvalidHeaderException(String message) {
    super(message);
  }
}

public class Header {
  private String key = "";
  private String value = "";

  public Header(String key) {
    setKey(key);
  }

  public Header(String key, String value) {
    setKey(key);
    setValue(value);
  }

  public static Header parseHeader(String header) throws InvalidHeaderException {
    String[] parts = header.split(":", 2);
    if (parts.length != 2) throw new InvalidHeaderException("Invalid header format");
    if (StringUtils.isBlank(parts[1])) throw new InvalidHeaderException("Invalid header format");
    return new Header(parts[0].trim(), parts[1].trim());
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) throws InvalidHeaderException {
    key = key.trim();
    if (StringUtils.isEmpty(key)) throw new InvalidHeaderException("Key is empty");
    this.key = WordUtils.capitalizeFully(key, '-');
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value.trim();
  }

  @Override
  public String toString() {
    return key + ": " + value;
  }
}
