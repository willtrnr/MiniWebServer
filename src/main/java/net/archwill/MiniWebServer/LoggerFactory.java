package net.archwill.MiniWebServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

final class LogFormatter extends Formatter {
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  @Override
  public String format(LogRecord record) {
    StringBuilder sb = new StringBuilder();
    sb.append(new Date(record.getMillis())).append(" - ").append(record.getLevel().getLocalizedName()).append(": ").append(formatMessage(record)).append(LINE_SEPARATOR);
    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        record.getThrown().printStackTrace(pw);
        pw.close();
        sb.append(sw.toString());
      } catch (Exception ex) {}
    }
    return sb.toString();
  }
}


public final class LoggerFactory {
  private static final LogFormatter FORMATTER = new LogFormatter();
  private static final HashMap<String, Logger> LOGGERS = new HashMap<String, Logger>();

  public static Logger getLogger(String name) {
    if (!LOGGERS.containsKey(name)) {
      Logger logger = Logger.getLogger(name);
      logger.setUseParentHandlers(false);
      Handler ch = new ConsoleHandler();
      ch.setFormatter(FORMATTER);
      logger.addHandler(ch);
      try {
        Handler fh = new FileHandler("./" + name + ".%u.log");
        fh.setFormatter(FORMATTER);
        logger.addHandler(fh);
      } catch (IOException e) {}
      LOGGERS.put(name, logger);
      return logger;
    } else {
      return LOGGERS.get(name);
    }
  }

  public static Logger getLogger() {
    return getLogger("MiniWebServer");
  }
}
