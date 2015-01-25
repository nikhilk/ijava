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

  public static void initializeLogging(Level logLevel,
                                       String logPath, int maxLogFileSize, int maxLogFileCount) {
    Logger globalLogger = Logger.getLogger("");
    globalLogger.setLevel(logLevel);

    Handler[] handlers = globalLogger.getHandlers();
    if (handlers[0] instanceof ConsoleHandler) {
      // Suppress default console handler
      globalLogger.removeHandler(handlers[0]);
    }

    boolean addConsoleHandler = logLevel == Level.FINE;
    Handler handler = null;
    if ((logPath != null) && !logPath.isEmpty()) {
      try {
        String logFilePattern = logPath + File.separator + Log.LOG_FILE_PATTERN;
        handler = new FileHandler(logFilePattern, maxLogFileSize, maxLogFileCount,
                                  /* append */ true);
      }
      catch (Exception e) {
      }
    }

    if (handler == null) {
      handler = new ConsoleHandler();
      addConsoleHandler = false;
    }

    handler.setLevel(logLevel);
    handler.setFormatter(new ConsoleLogFormatter());
    globalLogger.addHandler(handler);

    if (addConsoleHandler) {
      handler = new ConsoleHandler();
      handler.setLevel(logLevel);
      handler.setFormatter(new ConsoleLogFormatter());

      globalLogger.addHandler(handler);
    }
  }

  public void debug(String message, Object... objects) {
    _logger.fine(String.format(message, objects));
  }

  public void error(String message, Object... objects) {
    _logger.severe(String.format(message, objects));
  }

  public void exception(Exception e) {
    exception(e, "");
  }

  public void exception(Exception e, String message, Object... objects) {
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);

    if ((message != null) && !message.isEmpty()) {
      printWriter.append(String.format(message, objects)).append("\n");
    }
    e.printStackTrace(printWriter);
    printWriter.flush();

    error(writer.toString());
  }

  public void info(String message, Object... objects) {
    _logger.info(String.format(message, objects));
  }

  public void warn(String message, Object... objects) {
    _logger.warning(String.format(message, objects));
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
