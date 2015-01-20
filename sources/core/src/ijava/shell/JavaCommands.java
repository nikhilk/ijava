// JavaCommands.java
//

package ijava.shell;

import java.net.*;
import java.util.*;
import com.beust.jcommander.*;
import ijava.extensibility.*;

/**
 * Standard Java-language related commands.
 */
public final class JavaCommands {

  private JavaCommands() {
  }

  /**
   * Handles %dependency invocations to add dependencies to modules to subsequent compilations.
   */
  public static final class DependencyCommand extends Command<DependencyCommand.Options> {

    public DependencyCommand(Shell shell) {
      super(shell, Options.class);
    }

    @Override
    public Object evaluate(Options options, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      for (String uri: options.dependencies) {
        getShell().addDependency(URI.create(uri));
      }
      return null;
    }


    private static final class Options extends CommandOptions {

      @Parameter(description = "Dependency URIs")
      public List<String> dependencies = new ArrayList<String>();
    }
  }

  /**
   * Handles %jars invocations to list the current set of jar dependencies.
   */
  public static final class JarsCommand extends Command.SimpleCommand {

    public JarsCommand(Shell shell) {
      super(shell);
    }

    @Override
    public Object evaluate(long evaluationID, Map<String, Object> metadata) throws Exception {
      String[] jars = getShell().getReferences();
      Arrays.sort(jars);

      StringBuilder sb = new StringBuilder();
      for (String s: jars) {
        sb.append(s);
        sb.append("\n");
      }

      return sb.toString();
    }
  }

  /**
   * Handles %imports invocations to list the current set of imports.
   */
  public static final class ImportsCommand extends Command.SimpleCommand {

    public ImportsCommand(Shell shell) {
      super(shell);
    }

    @Override
    public Object evaluate(long evaluationID, Map<String, Object> metadata) throws Exception {
      String[] imports = getShell().getImports().split(";");
      Arrays.sort(imports);

      StringBuilder sb = new StringBuilder();
      for (String s: imports) {
        sb.append(s);
        sb.append(";\n");
      }

      return sb.toString();
    }
  }
}
