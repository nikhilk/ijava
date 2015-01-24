// ApplicationOptions.java
//

package ijava;

import java.util.*;
import java.util.logging.*;
import com.beust.jcommander.*;
import ijava.kernel.*;

@Parameters(separators = ":")
public final class ApplicationOptions {

  @Parameter(names = "--logLevel", converter = LogLevelConverter.class)
  public Level logLevel = Level.WARNING;

  @Parameter(names = "--logPath")
  public String logPath = null;

  @Parameter(names = "--logSize")
  public Integer logSize = 10485760;

  @Parameter(names = "--logFiles")
  public Integer logFiles = 10;

  @Parameter(names = "--dep")
  public List<String> dependencies = new ArrayList<String>();

  @Parameter(names = "--shellDep")
  public List<String> shellDependencies = new ArrayList<String>();

  @Parameter(names = "--ext")
  public List<String> extensions = new ArrayList<String>();

  @Parameter(arity = 1)
  public List<String> connectionFiles = new ArrayList<String>();

  @Parameter(names = { "--help", "--?" }, help = true)
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
      System.out.println("ijava [dependencies] [extensions] [logging] <connection> ");
      System.out.println();
      System.out.println("Dependencies");
      System.out.println("Optional list of jar paths (relative to ijava) that should be preload.");
      System.out.println("  --dep:<path to runtime dependency>");
      System.out.println("  --shellDep:<path to shell dependency>");
      System.out.println();
      System.out.println("Extensions");
      System.out.println("Optional list of extension classes to pre-load at startup.");
      System.out.println("  --ext:<fully qualified extension class name>");
      System.out.println();
      System.out.println("Logging");
      System.out.println("Optional set of logging configuration.");
      System.out.println("  --logLevel:<level>      debug, info (default), warning or error");
      System.out.println("  --logPath :<directory>  path to directory to generate log files into");
      System.out.println("  --logSize :<file size>  maximum file size for individual log files");
      System.out.println("  --logFiles:<file count> number of individual log files to rotate over");
      System.out.println();
      System.out.println("Connection");
      System.out.println("Path to JSON formatted file containing the following connection info:");
      System.out.println("- ip            the IP address to listen on");
      System.out.println("- transport     the transport to use such as 'tcp'");
      System.out.println("- hb_port       the socket port to send heart beat messages");
      System.out.println("- control_port  the socket port for sending control messages");
      System.out.println("- shell_port    the socket port for sending shell messages");
      System.out.println("- stdin_port    the socket port for sending input messages");
      System.out.println("- iopub_port    the socket port for sending broadcast messages");
      System.out.println();

      return null;
    }

    return options;
  }


  public static final class LogLevelConverter implements IStringConverter<Level> {

    @Override
    public Level convert(String value) {
      if (value.equals("debug")) {
        return Level.FINE;
      }
      else if (value.equals("info")) {
        return Level.INFO;
      }
      else if (value.equals("warn")) {
        return Level.WARNING;
      }
      else if (value.equals("error")) {
        return Level.SEVERE;
      }

      throw new IllegalArgumentException("Invalid value for log level.");
    }
  }
}
