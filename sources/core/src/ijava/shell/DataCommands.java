// DataCommands.java
//

package ijava.shell;

import java.util.*;
import com.beust.jcommander.*;
import com.fasterxml.jackson.jr.ob.*;
import ijava.data.*;
import ijava.extensibility.*;

/**
 * Provides commands to create or render data.
 */
public final class DataCommands {

  private DataCommands() {
  }

  /**
   * Handles %%html invocations to render HTML.
   */
  public static final class HTMLCommand extends Command.ContentCommand {

    public HTMLCommand(Shell shell) {
      super(shell);
    }

    @Override
    public Object evaluate(String content, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      return new HTML(content);
    }
  }

  /**
   * Handles %%javascript invocations to render HTML.
   */
  public static final class JavaScriptCommand extends Command.ContentCommand {

    public JavaScriptCommand(Shell shell) {
      super(shell);
    }

    @Override
    public Object evaluate(String content, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      return new JavaScript(content);
    }
  }

  /**
   * Handles %%text invocations to create a String instance.
   */
  public static final class TextCommand extends Command<DataCommandOptions> {

    public TextCommand(Shell shell) {
      super(shell, DataCommandOptions.class, /* singleLine */ false);
    }

    @Override
    public Object evaluate(DataCommandOptions options, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      if (!options.name.isEmpty()) {
        getShell().declareVariable(options.name, "String");
        getShell().setVariable(options.name, options.getContent());

        return null;
      }
      else {
        return options.getContent();
      }
    }
  }

  /**
   * Handles %%json invocations to parse a JSON formatted data.
   */
  public static final class JsonCommand extends Command<DataCommandOptions> {

    public JsonCommand(Shell shell) {
      super(shell, DataCommandOptions.class, /* singleLine */ false);
    }

    @Override
    public Object evaluate(DataCommandOptions options, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      Object value = null;
      String type = null;

      String data = options.getContent().trim();
      if (data.startsWith("{")) {
        value = JSON.std.mapFrom(data);
        type = "Map<String, Object>";
      }
      else if (data.startsWith("[")) {
        value = JSON.std.listFrom(data);
        type = "List<Object>";
      }
      else {
        throw new IllegalArgumentException("Invalid JSON. Must be either an object or an array.");
      }

      if (!options.name.isEmpty()) {
        getShell().declareVariable(options.name, type);
        getShell().setVariable(options.name, value);

        return null;
      }
      else {
        return value;
      }
    }
  }


  public static final class DataCommandOptions extends CommandOptions {

    @Parameter(names = "--name", description = "The name of the variable to create")
    public String name = "";
  }
}
