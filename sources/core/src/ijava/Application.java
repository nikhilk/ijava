// Application.java
//

package ijava;

import java.net.*;
import ijava.kernel.*;
import ijava.shell.*;

/**
 * Represents the entry point of the IJava kernel.
 */
public final class Application {

  /**
   * Application entry point method.
   * @param args the arguments passed in into the application process.
   */
  public static void main(String[] args) throws Exception {
    ApplicationOptions options = ApplicationOptions.parse(args);
    if (options == null) {
      System.exit(1);
    }

    Log.initializeLogging(options.logLevel, options.logPath, options.logSize, options.logFiles);

    URL applicationURL = Application.class.getProtectionDomain().getCodeSource().getLocation();

    InteractiveShell shell = new InteractiveShell();
    shell.initialize(applicationURL,
                     options.dependencies, options.shellDependencies, options.extensions);

    Session session = new Session(options.sessionOptions, shell);
    session.start();
  }
}
