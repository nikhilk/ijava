// Application.java
//

package ijava;

import ijava.kernel.*;
import sun.misc.*;

/**
 * Represents the entry point of the IJava kernel.
 */
public final class Application {

  private final Session _session;

  private Application(SessionOptions options) {
    _session = new Session(options);
  }

  /**
   * Application entry point method.
   * @param args the arguments passed in into the application process.
   */
  public static void main(String[] args) {
    if (args.length != 0) {
      SessionOptions options = SessionOptions.load(args[0]);
      if (options != null) {
        Application app = new Application(options);

        app.start();
        return;
      }
    }

    Application.showUsage();
    System.exit(1);
  }

  /**
   * Shows usage information about the application.
   */
  private static void showUsage() {
    System.out.println("Usage");
    System.out.println("ijava <options file>");
    System.out.println();
    System.out.println("Options file contains IPython kernel connection data (JSON formatted):");
    System.out.println("- ip");
    System.out.println("- transport");
    System.out.println("- hb_port");
    System.out.println("- control_port");
    System.out.println("- shell_port");
    System.out.println("- stdin_port");
    System.out.println("- iopub_port");
  }

  /**
   * Starts the application.
   */
  private void start() {
    InterruptHandler intHandler = new InterruptHandler();
    intHandler.register();

    _session.start();
  }

  /**
   * Stops the application.
   */
  private void stop() {
    _session.stop();
    System.exit(0);
  }


  /**
   * Handles INT interrupts.
   */
  private final class InterruptHandler implements SignalHandler {

    @Override
    public void handle(Signal sig) {
      stop();
    }

    public void register() {
      Signal.handle(new Signal("INT"), this);
    }
  }
}
