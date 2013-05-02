package com.willisite.MiniWebServer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {
  private static final Logger LOGGER = LoggerFactory.getLogger();

  private static Config INSTANCE = null;

  private String docRoot = ".";
  private int port = 8080;
  private Boolean listDir = false;

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
        setPort(Integer.parseInt(prop.getProperty("port", "8080")));
      } catch (NumberFormatException e) {
        LOGGER.warning("Invalid value for \"port\" in \"" + filename + "\", proceeding with default value");
      }
      setDocRoot(prop.getProperty("docroot", "."));
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
      prop.store(new FileOutputStream(filename), null);
    } catch (IOException e) {
      LOGGER.warning("Could not create \"" + filename + "\": " + e.getMessage());
    }
  }

  public String getDocRoot() {
    return docRoot;
  }

  public void setDocRoot(String docRoot) {
    this.docRoot = docRoot;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public Boolean getListDir() {
    return listDir;
  }

  public void setListDir(Boolean listDir) {
    this.listDir = listDir;
  }
}
