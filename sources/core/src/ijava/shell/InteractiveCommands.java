// InteractiveCommands.java
//

package ijava.shell;

import java.util.*;
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
    public Object evaluate(String arguments, String data, int evaluationID,
                           Map<String, Object> metadata) throws Exception {
      if ((arguments == null) || arguments.isEmpty()) {
        throw new EvaluationError("The name of the extension to load must be specified.");
      }

      return _shell.addExtension(arguments);
    }
  }
}
