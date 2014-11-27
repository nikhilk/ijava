// ExtensionData.java
//

package ijava.shell;

import java.util.*;

/**
 * Represents the data for an extension invocation.
 */
public final class ExtensionData {

  private final String _name;
  private final String _declaration;
  private final String _content;

  /**
   * Initializes an instance of an ExtensionData.
   * @param name the name of the extension.
   * @param declaration the declaration for the extension.
   * @param content the content for the extension.
   */
  public ExtensionData(String name, String declaration, String content) {
    _name = name;
    _declaration = declaration;
    _content = content;
  }

  /**
   * Gets the content associated with the extension.
   * @return the content for the extension.
   */
  public String getContent() {
    return _content;
  }

  /**
   * Gets the declaration associated with the extension.
   * @return the declaration for the extension.
   */
  public String getDeclaration() {
    return _declaration;
  }

  /**
   * Gets the name of the extension to be invoked.
   * @return the name of the extension.
   */
  public String getName() {
    return _name;
  }

  /**
   * Parses the declaration into a set of options, as if it were a set of command-line args.
   * @return the parsed set of flags.
   */
  public Map<String, Object> parseOptions() {
    HashMap<String, Object> options = new HashMap<String, Object>();

    String[] args = _declaration.split(" ");
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
