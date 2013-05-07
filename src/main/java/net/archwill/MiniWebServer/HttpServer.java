package net.archwill.MiniWebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class HttpServer implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger();
  private final Thread t;

  public HttpServer() {
    t = new Thread(this);
  }

  public void start() {
    t.start();
  }

  @Override
  public void run() {
    try {
      ServerSocket server = new ServerSocket(Config.Instance().getPort());
      LOGGER.info("MiniWebServer serving \"" + Config.Instance().getDocRoot() + "\" on port " + Integer.toString(Config.Instance().getPort()));
      while (true) {
        try {
          Socket client = server.accept();
          ConnectionHandler handler = new ConnectionHandler(client);
          handler.start();
        } catch (IOException e) {
          LOGGER.warning("Client IOException: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      LOGGER.severe("Server IOException: " + e.getMessage());
      System.exit(1);
    }
  }
}
