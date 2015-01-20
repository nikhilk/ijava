// CommandData.java
//

package ijava.shell;

import java.util.regex.*;
import ijava.extensibility.*;
import com.beust.jcommander.*;

/**
 * Represents the data for an command invocation.
 */
public final class CommandData {

  private final static Pattern CommandPattern;

  private final String _name;
  private final String[] _arguments;
  private final String _content;

  static {
    CommandPattern = Pattern.compile("^%%?(?<name>[a-z\\._]+)(\\s+)?(?<args>.*)?$",
                                     Pattern.CASE_INSENSITIVE);
  }

  /**
   * Initializes an instance of an CommandData.
   * @param name the name of the command to evaluate.
   * @param arguments the arguments to the command.
   * @param content the content for the command.
   */
  private CommandData(String name, String arguments, String content) {
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

  /**
   * Parses text representing an command invocation.
   * @param data the text to parse.
   * @return a parsed CommandData object.
   */
  public static CommandData parse(String data) {
    String name = null;
    String args = null;
    String content = null;

    if (data.startsWith("%%")) {
      int newLineIndex = data.indexOf('\n');
      if ((newLineIndex < 0) || (data.length() <= (newLineIndex + 1))) {
        content = "";
      }
      else {
        content = data.substring(newLineIndex + 1);

        // Strip off everything starting with the first new line. Also strip out a leading '%'
        // so the resulting data matches the syntax of single line commands.
        data = data.substring(1, newLineIndex);
      }
    }

    Matcher matcher = CommandData.CommandPattern.matcher(data);
    if (!matcher.matches()) {
      return null;
    }

    name = matcher.group("name");
    args = matcher.group("args");

    return new CommandData(name, args, content);
  }

  /**
   * Converts a command data into an options object as expected by the specified command.
   * @param command the command that should be used to create the resulting options object.
   * @return the initialized options object, or null if there was an error.
   */
  public CommandOptions toOptions(Command<?> command) {
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
