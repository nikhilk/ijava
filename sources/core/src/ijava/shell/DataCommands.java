// DataCommands.java
//

package ijava.shell;

import java.util.*;
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
  public static final class HTMLCommand implements Command {

    public HTMLCommand(Shell shell) {
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      return new HTML(data);
    }
  }

  /**
   * Handles %%javascript invocations to render HTML.
   */
  public static final class JavaScriptCommand implements Command {

    public JavaScriptCommand(Shell shell) {
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      return new JavaScript(data);
    }
  }

  /**
   * Handles %%text invocations to create a String instance.
   */
  public static final class TextCommand implements Command {

    private final Shell _shell;

    public TextCommand(Shell shell) {
      _shell = shell;
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      if (arguments.length() != 0) {
        _shell.declareVariable(arguments, "String");
        _shell.setVariable(arguments, data);

        return null;
      }
      else {
        return data;
      }
    }
  }

  /**
   * Handles %%json invocations to parse a JSON formatted data.
   */
  public static final class JsonCommand implements Command {

    private final Shell _shell;

    public JsonCommand(Shell shell) {
      _shell = shell;
    }

    @Override
    public Object evaluate(String arguments, String data, long evaluationID,
                           Map<String, Object> metadata) throws Exception {
      Object value = null;
      String name = null;

      data = data.trim();
      if (data.startsWith("{")) {
        value = JSON.std.mapFrom(data);
        name = "Map<String, Object>";
      }
      else if (data.startsWith("[")) {
        value = JSON.std.listFrom(data);
        name = "List<Object>";
      }
      else {
        throw new IllegalArgumentException("Invalid JSON. Must be either an object or an array.");
      }

      if (arguments.length() != 0) {
        _shell.declareVariable(arguments, name);
        _shell.setVariable(arguments, value);

        return null;
      }
      else {
        return value;
      }
    }
  }
}
