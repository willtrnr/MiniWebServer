package com.willisite.MiniWebServer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/*
HTTP/1.1 418 I'm a teapot
Content-Length: 281
Content-Type: text/plain
Connection: close
Server: MiniWebServer

                       (
            _           ) )
         _,(_)._        ((
    ___,(_______).        )
  ,'__.   /       \    /\_
 /,' /  |""|       \  /  /
| | |   |__|       |,'  /
 \`.|                  /
  `. :           :    /
    `.            :.,'
      `-.________,-'
*/

public final class MiniWebServer {
  private static Logger LOGGER = LoggerFactory.getLogger();

  public static void main(String[] args) {
    String docRoot = ".";
    int port = 8080;

    try {
      Properties prop = new Properties();
      prop.load(new FileInputStream("server.conf"));
      try {
        port = Integer.parseInt(prop.getProperty("port", "8080"));
      } catch (NumberFormatException e) {
        LOGGER.warning("Invalid value for \"port\" in \"server.conf\", proceeding with default value");
      }
      docRoot = prop.getProperty("docroot", ".");
    } catch (IOException e) {
      LOGGER.info("Creating \"server.conf\" with default values");
      try {
        Properties prop = new Properties();
        prop.setProperty("port", Integer.toString(port));
        prop.setProperty("docroot", docRoot);
        prop.store(new FileOutputStream("server.conf"), null);
      } catch (IOException ex) {
        LOGGER.warning("Could not create \"server.conf\": " + e.getMessage());
      }
    }

    try {
      if (args.length >= 1) port = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      LOGGER.warning("Could parse \"port\", proceeding with default value");
    }
    if (args.length >= 2) docRoot = args[1];

    LOGGER.info("");
    LOGGER.info("HTTP/1.1 418 I'm a teapot");
    LOGGER.info("Content-Length: 281");
    LOGGER.info("Content-Type: text/plain");
    LOGGER.info("Connection: close");
    LOGGER.info("Srver: MiniwebServer");
    LOGGER.info("");
    LOGGER.info("                       (");
    LOGGER.info("            _           ) )");
    LOGGER.info("         _,(_)._        ((");
    LOGGER.info("    ___,(_______).        )");
    LOGGER.info("  ,'__.   /       \\    /\\_");
    LOGGER.info(" /,' /  |\"\"|       \\  /  /");
    LOGGER.info("| | |   |__|       |,'  /");
    LOGGER.info(" \\`.|                  /");
    LOGGER.info("  `. :           :    /");
    LOGGER.info("    `.            :.,'");
    LOGGER.info("      `-.________,-'");
    LOGGER.info("");
    HttpServer server = new HttpServer(port, docRoot);
    server.start();
    while (true) {
      try {
        if (System.console().readLine().trim().toLowerCase().charAt(0) == 'q') System.exit(0);
      } catch (StringIndexOutOfBoundsException e) {
      } catch (NullPointerException e) {}
    }
  }
}
