package com.willisite.MiniWebServer;

import org.apache.commons.lang3.text.WordUtils;

class InvalidHeaderException extends Exception {
  public InvalidHeaderException(String message) {
    super(message);
  }
}

public class Header {
  private String key;
  private String value;

  public static Header parseHeader(String header) {

  }

  public Header(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = WordUtils.capitalizeFully(key, '-');
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
