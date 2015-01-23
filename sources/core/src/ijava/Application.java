// Application.java
//

package ijava;

import java.net.*;
import java.util.logging.*;
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

    URL applicationURL = Application.class.getProtectionDomain().getCodeSource().getLocation();

    InteractiveShell shell = new InteractiveShell();
    shell.initialize(applicationURL,
                     options.dependencies, options.shellDependencies, options.extensions);

    // TODO: Make this customizable via command line
    Log.initializeLogging(Level.INFO);

    Session session = new Session(options.sessionOptions, shell);
    session.start();
  }
}
