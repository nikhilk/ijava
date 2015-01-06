// JavaCommands.java
//

package ijava.shell;

import java.net.*;
import java.util.*;
import com.fasterxml.jackson.jr.ob.*;
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
  public static final class DependencyCommand implements Command {

    private final Shell _shell;

    public DependencyCommand(Shell shell) {
      _shell = shell;
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      if (arguments.startsWith("'") || arguments.startsWith("\"")) {
        arguments = arguments.substring(1, arguments.length() - 1);
      }

      _shell.addDependency(URI.create(arguments));
      return null;
    }
  }

  /**
   * Handles %jars invocations to list the current set of jar dependencies.
   */
  public static final class JarsCommand implements Command {

    private final Shell _shell;

    public JarsCommand(Shell shell) {
      _shell = shell;
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      String[] jars = _shell.getReferences();
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
  public static final class ImportsCommand implements Command {

    private final Shell _shell;

    public ImportsCommand(Shell shell) {
      _shell = shell;
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      String[] imports = _shell.getImports().split(";");
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
