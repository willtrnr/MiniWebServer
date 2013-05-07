package net.archwill.MiniWebServer;

import java.util.logging.Logger;

/*
HTTP/1.1 418 I'm a teapot
Content-Length: 281
Content-Type: text/plain
Connection: close
Server: MiniWebServer/0.0.1

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
    try {
      if (args.length >= 1) Config.Instance().setPort(Integer.parseInt(args[0]));
    } catch (NumberFormatException e) {
      LOGGER.warning("Could not parse \"port\", proceeding with default value");
    }
    if (args.length >= 2) Config.Instance().setDocRoot(args[1]);

    LOGGER.info("");
    LOGGER.info("HTTP/1.1 418 I'm a teapot");
    LOGGER.info("Content-Length: 281");
    LOGGER.info("Content-Type: text/plain");
    LOGGER.info("Connection: close");
    LOGGER.info("Srver: MiniwebServer/0.0.1");
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
    HttpServer server = new HttpServer();
    server.start();
    while (true) {
      try {
        if (System.console().readLine().trim().toLowerCase().charAt(0) == 'q') System.exit(0);
      } catch (StringIndexOutOfBoundsException e) {
      } catch (NullPointerException e) {}
    }
  }
}
