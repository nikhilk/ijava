// Application.java
//

package ijava;

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
  public static void main(String[] args) {
    boolean showUsage = true;

    if (args.length != 0) {
      SessionOptions options = SessionOptions.load(args[0]);
      if (options != null) {
        showUsage = false;

        InteractiveShell shell = new InteractiveShell();
        Session session = new Session(options, shell);

        session.start();
      }
    }

    if (showUsage) {
      System.out.println("Usage:");
      System.out.println("ijava <connection file>");
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

      System.exit(1);
    }
  }
}
