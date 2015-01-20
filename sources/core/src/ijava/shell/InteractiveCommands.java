// InteractiveCommands.java
//

package ijava.shell;

import java.util.*;
import com.beust.jcommander.*;
import ijava.data.*;
import ijava.extensibility.*;

/**
 * Implements commands provided by the shell itself.
 */
public final class InteractiveCommands {

  private InteractiveCommands() {
  }

  public static final class LoadCommand extends Command<LoadCommand.Options> {

    public LoadCommand(InteractiveShell shell) {
      super(shell, Options.class);
    }

    @Override
    public Object evaluate(Options options, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      return ((InteractiveShell)getShell()).addExtension(options.extension);
    }

    public static final class Options extends CommandOptions {

      @Parameter(names = "--ext", description = "The name of the extension to load.",
          required = true)
      public String extension;
    }
  }

  public static final class ValuesCommand extends Command<ValuesCommand.Options> {

    public ValuesCommand(InteractiveShell shell) {
      super(shell, Options.class);
    }

    @Override
    public Object evaluate(Options options, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      HashMap<String, Object> values = new HashMap<String, Object>();

      InteractiveState state = ((InteractiveShell)getShell()).getState();
      Set<String> variables = state.getFields();

      for (String name: options.names) {
        if (variables.contains(name)) {
          values.put(name, state.getValue(name));
        }
      }

      return new Data(values);
    }

    public static final class Options extends CommandOptions {

      @Parameter(description = "The names of the values to return.")
      public List<String> names = new ArrayList<String>();
    }
  }
}
