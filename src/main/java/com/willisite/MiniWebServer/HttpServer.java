package com.willisite.MiniWebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class HttpServer implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger("HttpServer");
  private final Thread t;

  private int port = 80;
  private String docRoot = ".";

  public HttpServer() {
    t = new Thread(this);
  }

  public HttpServer(int port, String docRoot) {
    this();
    this.port = port;
    this.docRoot = docRoot;
  }

  public void start() {
    t.start();
  }

  @Override
  public void run() {
    try {
      ServerSocket server = new ServerSocket(port);
      LOGGER.info("MiniWebServer serving \"" + docRoot + "\" on port " + Integer.toString(port));
      while (true) {
        try {
          Socket client = server.accept();
          ConnectionHandler handler = new ConnectionHandler(client, docRoot);
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
