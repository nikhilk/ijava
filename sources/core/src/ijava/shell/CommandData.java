// CommandData.java
//

package ijava.shell;

import ijava.extensibility.*;
import com.beust.jcommander.*;

/**
 * Represents the data for an command invocation.
 */
public final class CommandData {

  private final String _name;
  private final String[] _arguments;
  private final String _content;

  /**
   * Initializes an instance of an CommandData.
   * @param name the name of the command to evaluate.
   * @param arguments the arguments to the command.
   * @param content the content for the command.
   */
  public CommandData(String name, String arguments, String content) {
    _name = name;
    _arguments = arguments.split(" ");
    _content = content;
  }

  /**
   * Gets the name of the command to be invoked.
   * @return the name of the command.
   */
  public String getName() {
    return _name;
  }

  public CommandOptions parseOptions(Command<?> command) {
    CommandOptions options;
    try {
      options = command.createOptions();
    }
    catch (Exception e) {
      // TODO: Log
      return null;
    }

    JCommander optionsParser = options.createParser(_name, _arguments, _content);
    boolean parseError = false;
    try {
      optionsParser.parse(_arguments);
    }
    catch (ParameterException e) {
      parseError = true;
    }

    if (parseError || options.help) {
      optionsParser.usage();
      if (!command.isSingleLine()) {
        System.out.println("[ ... additional content associated with command]");
      }

      return null;
    }

    options.setCommand(optionsParser.getParsedCommand());
    return options;
  }
}
