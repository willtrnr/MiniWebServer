package com.willisite.MiniWebServer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

public class ConnectionHandler implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger("ConnectionHandler");
  private static final String TEAPOT = "                       (\n            _           ) )\n         _,(_)._        ((\n    ___,(_______).        )\n  ,'__.   /       \\    /\\_\n /,' /  |\"\"|       \\  /  /\n| | |   |__|       |,'  /\n \\`.|                  /\n  `. :           :    /\n    `.            :.,'\n      `-.________,-'\n";
  private final Thread t;


  private Socket client;
  private String docRoot;

  public ConnectionHandler(Socket client, String docRoot) {
    t = new Thread(this);
    this.client = client;
    this.docRoot = docRoot;
  }

  public void start() {
    t.start();
  }

  @Override
  public void run() {
    try {
      for (int maxRequests = 3; maxRequests >= 1; --maxRequests) {
        try {
          Request request = new Request(client.getInputStream());
          Response response = new Response(request);
          if (response.getHeader("Connection") == null || response.getHeader("Connection").getValue().equals("close")) maxRequests = 1;
          if (maxRequests == 1) response.setHeader("Connection", "close");

          if (request.getUri().getPath().equals("/teapot")) {
            response.setStatusCode(418);
            response.setHeader("Content-Type", "text/plain");
            response.send(client.getOutputStream(), TEAPOT.getBytes());
          } else {
            // OMG OMG OMG OMG TO FUCKING DO: FIX THIS SECURITY HOLE AND SHIT HANDLING ASAP
            File file = new File(docRoot, request.getUri().getPath());
            if (file.isFile()) {
              response.setStatusCode(200);
              response.send(client.getOutputStream(), file);
            } else if (file.isDirectory()) {
              file = new File(file, "index.html");
              if (file.isFile()) {
                response.setStatusCode(200);
                response.send(client.getOutputStream(), file);
              } else {
                response.setStatusCode(403);
                response.sendHeaders(client.getOutputStream());
              }
            } else {
              response.setStatusCode(404);
              response.sendHeaders(client.getOutputStream());
            }
          }
        } catch (InterruptedRequestException e) {
          break;
        } catch (InvalidRequestException e) {
          Response response = new Response(400);
          response.sendHeaders(client.getOutputStream());
          LOGGER.warning(e.getMessage());
        } catch (URISyntaxException e) {
          Response response = new Response(400);
          response.sendHeaders(client.getOutputStream());
          LOGGER.warning(e.getMessage());
        } catch (InvalidHeaderException e) {
          Response response = new Response(400);
          response.sendHeaders(client.getOutputStream());
          LOGGER.warning(e.getMessage());
        }
      }
      client.close();
    } catch (IOException e) {
      LOGGER.severe(e.getMessage());
    }
  }
}
