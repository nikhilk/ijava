// Application.java
//

package ijava;

public final class Application {

  private final ApplicationOptions _options;

  private Application(ApplicationOptions options) {
    _options = options;
  }

  public static void main(String[] args) {
    ApplicationOptions options = null;
    if (args.length != 0) {
      options = ApplicationOptions.load(args[0]);
    }

    if (options == null) {
      Application.showUsage();
      System.exit(-1);
    }

    try {
      Application app = new Application(options);
      app.run();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void run() {
    System.out.println(_options.toString());
  }

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
}
