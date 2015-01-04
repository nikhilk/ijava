// CommandData.java
//

package ijava.shell;

import java.util.*;

/**
 * Represents the data for an command invocation.
 */
public final class CommandData {

  private final String _name;
  private final String _arguments;
  private final String _content;

  /**
   * Initializes an instance of an CommandData.
   * @param name the name of the command to evaluate.
   * @param arguments the arguments to the command.
   * @param content the content for the command.
   */
  public CommandData(String name, String arguments, String content) {
    _name = name;
    _arguments = arguments;
    _content = content;
  }

  /**
   * Gets the arguments associated with the command.
   * @return the arguments for the command.
   */
  public String getArguments() {
    return _arguments;
  }

  /**
   * Gets the content associated with the command.
   * @return the content for the command.
   */
  public String getContent() {
    return _content;
  }

  /**
   * Gets the name of the command to be invoked.
   * @return the name of the command.
   */
  public String getName() {
    return _name;
  }

  /**
   * Parses the arguments into a set of options.
   * @return the parsed set of flags.
   */
  public Map<String, Object> parseOptions() {
    HashMap<String, Object> options = new HashMap<String, Object>();

    String[] args = _arguments.split(" ");
    int i = 0;

    while (i < args.length) {
      String arg = args[i];

      if (arg.equals("--")) {
        // Treat this as separator between named options and positional options
        i++;
        break;
      }
      else if (arg.startsWith("-") && (arg.length() > 1)) {
        String name = arg.substring(1);
        i++;

        if ((i < args.length) && !args[i].startsWith("-")) {
          // Consume the next arg as the value
          String value = args[i];
          i++;

          options.put(name, value);
          i++;
        }
        else {
          // Treat this as a boolean option
          options.put(name, true);
        }
      }
      else {
        // Consider this as the start of positional options
        break;
      }
    }

    if (i < args.length) {
      // Treat remaining args as positional options
      String[] values = Arrays.copyOfRange(args, i, args.length - 1);
      options.put("...", values);
    }

    return options;
  }
}
