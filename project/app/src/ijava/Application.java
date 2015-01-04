// Application.java
//

package ijava;

import java.net.*;
import java.util.*;
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
    boolean showUsage = true;

    if (args.length != 0) {
      SessionOptions options = SessionOptions.load(args[0]);
      if (options != null) {
        showUsage = false;

        URL applicationURL = Application.class.getProtectionDomain().getCodeSource().getLocation();
        List<String> dependencies = new ArrayList<String>();
        List<String> extensions = new ArrayList<String>();

        for (int i = 1; i < args.length; i += 2) {
          if (args[i].equals("-d")) {
            dependencies.add(args[i + 1]);
          }
          else if (args[i].equals("-e")) {
            extensions.add(args[i + 1]);
          }
        }

        InteractiveShell shell = new InteractiveShell();
        shell.initialize(applicationURL, dependencies, extensions);

        // TODO: Make this customizable via command line
        Log.initializeLogging(Level.INFO);

        Session session = new Session(options, shell);
        session.start();
      }
    }

    if (showUsage) {
      System.out.println("Usage:");
      System.out.println("ijava <connection file> [jars...]");
      System.out.println();
      System.out.println("Connection file contains JSON formatted data for the following:");
      System.out.println("- ip            the IP address to listen on");
      System.out.println("- transport     the transport to use such as 'tcp'");
      System.out.println("- hb_port       the socket port to send heart beat messages");
      System.out.println("- control_port  the socket port for sending control messages");
      System.out.println("- shell_port    the socket port for sending shell messages");
      System.out.println("- stdin_port    the socket port for sending input messages");
      System.out.println("- iopub_port    the socket port for sending broadcast messages");
      System.out.println();
      System.out.println("Jars is a list of dependencies to pre-load at startup.");
      System.out.println("  <path to jar (relative to ijava)> [0...N]");
      System.out.println();

      System.exit(1);
    }
  }
}
