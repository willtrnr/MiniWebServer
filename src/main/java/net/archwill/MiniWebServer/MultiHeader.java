package net.archwill.MiniWebServer;

import java.util.ArrayList;
import java.util.List;

public class MultiHeader extends Header {
  private List<String> values = new ArrayList<String>();

  public MultiHeader(String key) {
    super(key);
  }

  public MultiHeader(String key, String value) {
    super(key);
    setValue(value);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (!values.contains(value.trim())) values.add(value.trim());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String value : values) {
      sb.append(getKey()).append(": ").append(value).append("\r\n");
    }
    return sb.toString().trim();
  }
}
