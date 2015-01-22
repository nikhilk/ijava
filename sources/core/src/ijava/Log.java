// Log.java
//

package ijava;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

/**
 * Provides logging functionality.
 */
public final class Log {

  private final static String LOG_FILE_PATTERN = "ijava.%g.log";

  private final Logger _logger;

  private Log(Logger logger) {
    _logger = logger;
  }

  public static Log createLog(String name) {
    Logger logger = Logger.getLogger(name);
    logger.setUseParentHandlers(true);
    return new Log(logger);
  }

  public static void initializeLogging(Level logLevel) {
    Log.initializeLogging(logLevel,
                          /* logPath */ null, /* maxLogFileSize */ 0, /* maxLogFileCount */ 0);
  }

  public static void initializeLogging(Level logLevel,
                                       String logPath, int maxLogFileSize, int maxLogFileCount) {
    // Suppress default
    Logger logger = Logger.getLogger("");
    Handler[] handlers = logger.getHandlers();
    if (handlers[0] instanceof ConsoleHandler) {
      logger.removeHandler(handlers[0]);
    }

    // Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    logger.setLevel(logLevel);

    if ((logPath != null) && !logPath.isEmpty()) {
      try {
        String logFilePattern = logPath + File.separator + Log.LOG_FILE_PATTERN;

        FileHandler fileHandler = new FileHandler(logFilePattern, maxLogFileSize, maxLogFileCount,
                                                  /* append */ true);
        fileHandler.setLevel(logLevel);
        // TODO: Set Formatter

        logger.addHandler(fileHandler);
      }
      catch (Exception e) {
      }
    }
    else {
      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setLevel(logLevel);
      consoleHandler.setFormatter(new ConsoleLogFormatter());

      logger.addHandler(consoleHandler);
    }
  }

  public void debug(String message) {
    _logger.fine(message);
  }

  public void error(String message) {
    _logger.severe(message);
  }

  public void info(String message) {
    _logger.info(message);
  }

  public void warn(String message) {
    _logger.warning(message);
  }


  private static final class ConsoleLogFormatter extends java.util.logging.Formatter {

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
      StringBuilder builder = new StringBuilder(1000);

      String timestamp = ConsoleLogFormatter.df.format(new Date(record.getMillis()));
      String level = "";
      if (record.getLevel() == Level.FINE) {
        level = "DEBUG";
      }
      else if (record.getLevel() == Level.WARNING) {
        level = " WARN";
      }
      else if (record.getLevel() == Level.INFO) {
        level = " INFO";
      }
      else if (record.getLevel() == Level.SEVERE) {
        level = "ERROR";
      }

      builder.append(timestamp).append(" ");
      builder.append(level).append(" ");
      builder.append(record.getLoggerName()).append(": ");
      builder.append(formatMessage(record)).append("\n");

      return builder.toString();
    }
  }
}
