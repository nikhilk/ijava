// InteractiveCommands.java
//

package ijava.shell;

import java.util.*;
import ijava.data.*;
import ijava.extensibility.*;

/**
 * Implements commands provided by the shell itself.
 */
public final class InteractiveCommands {

  private InteractiveCommands() {
  }

  public static final class LoadCommand implements Command {

    private final InteractiveShell _shell;

    public LoadCommand(InteractiveShell shell) {
      _shell = shell;
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      if ((arguments == null) || arguments.isEmpty()) {
        throw new EvaluationError("The name of the extension to load must be specified.");
      }

      return _shell.addExtension(arguments);
    }
  }

  public static final class ValuesCommand implements Command {

    private final InteractiveShell _shell;

    public ValuesCommand(InteractiveShell shell) {
      _shell = shell;
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      if ((arguments == null) || arguments.isEmpty()) {
        throw new EvaluationError("The values to retrive must be specified.");
      }

      HashMap<String, Object> values = new HashMap<String, Object>();

      InteractiveState state = _shell.getState();
      Set<String> variables = state.getFields();

      for (String name: arguments.split(",")) {
        if (variables.contains(name)) {
          values.put(name, state.getValue(name));
        }
      }

      return new Data(values);
    }
  }
}
