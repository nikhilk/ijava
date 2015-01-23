// ApplicationOptions.java
//

package ijava;

import java.util.*;
import com.beust.jcommander.*;
import ijava.kernel.*;

public final class ApplicationOptions {

  @Parameter(names = "--dep", description = "The path to a jar to load as a dependency.")
  public List<String> dependencies = new ArrayList<String>();

  @Parameter(names = "--shellDep", description = "The path to a jar to load as a shell dependency.")
  public List<String> shellDependencies = new ArrayList<String>();

  @Parameter(names = "--ext", description = "The name of an extension to pre-load")
  public List<String> extensions = new ArrayList<String>();

  @Parameter(description = "The path to the connection information file")
  public List<String> connectionFiles = new ArrayList<String>();

  @Parameter(names = "--help", description = "Show usage information.", help = true)
  public boolean showHelp = false;

  public SessionOptions sessionOptions = null;

  private ApplicationOptions() {
  }

  public static ApplicationOptions parse(String[] args) {
    ApplicationOptions options = new ApplicationOptions();
    JCommander commandParser = new JCommander(options);

    String error = "";
    try {
      commandParser.parse(args);

      if (!options.connectionFiles.isEmpty()) {
        options.sessionOptions = SessionOptions.load(options.connectionFiles.get(0));
      }
    }
    catch (ParameterException e) {
      error = e.getMessage();
    }

    if (options.showHelp || !error.isEmpty() || (options.sessionOptions == null)) {
      System.out.println(error);

      System.out.println("Usage:");
      System.out.println("ijava [jars...] [exts...] <connection file> ");
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
      System.out.println("Jars is an optional list of dependencies to pre-load at startup.");
      System.out.println("  Each jar is specified as a path relative to the location of ijava.");
      System.out.println("  --dep <path to runtime dependency>");
      System.out.println("  --shellDep <path to shell dependency>");
      System.out.println();
      System.out.println("Exts is an optional list of extensions to pre-load at startup.");
      System.out.println("  Each extensions is specified using the package-qualified class name.");
      System.out.println("  --ext <extension class name>");

      return null;
    }

    return options;
  }
}
