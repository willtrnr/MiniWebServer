package net.archwill.MiniWebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {
  private static final Logger LOGGER = LoggerFactory.getLogger();

  private static Config INSTANCE = null;

  private String docRoot = ".";
  private int port = 80;
  private Boolean listDir = false;
  private int maxReq = 5;

  public static Config Instance() {
    if (INSTANCE == null) {
      INSTANCE = new Config();
      INSTANCE.load("server.conf");
    }
    return INSTANCE;
  }

  public void load(String filename) {
    try {
      Properties prop = new Properties();
      prop.load(new FileInputStream(filename));
      try {
        setPort(Integer.parseInt(prop.getProperty("port", "80")));
      } catch (NumberFormatException e) {
        LOGGER.warning("Invalid value for \"port\" in \"" + filename + "\", proceeding with default value");
      }
      setDocRoot(prop.getProperty("docroot", "."));
      setListDir(prop.getProperty("listdir", "0").equals("1") || prop.getProperty("listdir", "false").toLowerCase().equals("true"));
      try {
        setMaxReq(Integer.parseInt(prop.getProperty("maxreq", "5")));
      } catch (NumberFormatException e) {
        LOGGER.warning("Invalid value for \"maxreq\" in \"" + filename + "\", proceeding with default value");
      }
    } catch (IOException e) {
      LOGGER.info("Creating \"" + filename + "\" with default values");
      save(filename);
    }
  }

  public void save(String filename) {
    try {
      Properties prop = new Properties();
      prop.setProperty("port", Integer.toString(getPort()));
      prop.setProperty("docroot", getDocRoot());
      prop.setProperty("listdir", (getListDir()) ? "1" : "0");
      prop.setProperty("maxreq", Integer.toString(getMaxReq()));
      prop.store(new FileOutputStream(filename), null);
    } catch (IOException e) {
      LOGGER.warning("Could not create \"" + filename + "\": " + e.getMessage());
    }
  }

  public String getDocRoot() {
    return docRoot;
  }

  public void setDocRoot(String docRoot) {
    if (!new File(docRoot).isDirectory()) LOGGER.warning("The \"docroot\" does not exist, proceeding anyway");
    this.docRoot = docRoot;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    if (port >= 1 && port <= 32767) this.port = port;
    else LOGGER.warning("Invalid value for \"port\", proceeding with default value");
  }

  public Boolean getListDir() {
    return listDir;
  }

  public void setListDir(Boolean listDir) {
    this.listDir = listDir;
  }

  public int getMaxReq() {
    return maxReq;
  }

  public void setMaxReq(int maxReq) {
    this.maxReq = maxReq;
  }
}
