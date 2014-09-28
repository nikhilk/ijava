// Application.java
//

package ijava;

import ijava.kernel.*;

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

        // Start kicks off the session, and doesn't return unless there is an exception.
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
    new ShutdownHandler().installHook();
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
   * Implements shutdown logic.
   */
  private final class ShutdownHandler implements Runnable {

    /**
     * Installs this instance as the shutdown hook.
     */
    public void installHook() {
      Runtime.getRuntime().addShutdownHook(new Thread(this));
    }

    /**
     * {@link} Runnable
     */
    @Override
    public void run() {
      Thread.currentThread().setName("Shutdown Handler");
      try {
        _session.stop();
      }
      catch (Exception e) {
      }
    }
  }
}
