package net.archwill.MiniWebServer;

public class Redirect extends Response {
  public Redirect() {
    super();
    setStatusCode(301);
  }

  public Redirect(String location)  {
    this();
    setHeader("Location", location);
  }

  public Redirect(String location, int statusCode) {
    setStatusCode(statusCode);
    setHeader("Location", location);
  }

  public String getLocation() {
    Header location = getHeader("Location");
    if (location != null) return location.getValue();
    return null;
  }

  public void setLocation(String location) {
    setHeader("Location", location);
  }
}
