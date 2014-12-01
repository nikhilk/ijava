// Log.java
//

package ijava;

import java.io.*;
import java.util.logging.*;

/**
 * Provides logging functionality.
 */
public final class Log {

  private final static String LOG_FILE_PATTERN = "ijava.%g.log";
  private static Logger _logger;

  private Log() {
  }

  public static void initializeLogging(Level logLevel) {
    Log.initializeLogging(logLevel,
                          /* logPath */ null, /* maxLogFileSize */ 0, /* maxLogFileCount */ 0);
  }

  public static void initializeLogging(Level logLevel,
                                       String logPath, int maxLogFileSize, int maxLogFileCount) {
    Logger logger = Logger.getAnonymousLogger();
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

    Log._logger = logger;
  }

  public static void logDebug(String message) {
    Log._logger.fine(message);
  }

  public static void logError(String message) {
    Log._logger.severe(message);
  }

  public static void logInfo(String message) {
    Log._logger.info(message);
  }

  public static void logWarning(String message) {
    Log._logger.warning(message);
  }
}
