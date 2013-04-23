package com.willisite.MiniWebServer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
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
      client.setSoTimeout(30 * 1000);
      try {
        Request request = new Request(client.getInputStream());
        Response response = request.createResponse();

        if (request.getUri().equals("/")) {
          response.setStatusCode(418);
          response.setHeader("Content-Type", "text/plain; charset=utf-8");
          response.send(client.getOutputStream(), TEAPOT.getBytes());
        } else {
          // OMG OMG OMG OMG TO FUCKING DO: FIX THIS SECURITY HOLE AND SHIT HANDLING ASAP
          File file = new File(docRoot, request.getUri());
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
      } catch (InvalidRequestException e) {
        Response response = new Response(400);
        response.sendHeaders(client.getOutputStream());
      } catch (InvalidHeaderException e) {
        Response response = new Response(400);
        response.sendHeaders(client.getOutputStream());
      }

      client.close();
    } catch (IOException e) {
      LOGGER.throwing("ConnectionHandler", "run", e);
    }
  }
}
