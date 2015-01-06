// CommandDataParser.java
//

package ijava.shell;

import java.util.regex.*;

/**
 * Parses commands to be evaluated in the shell.
 */
public final class CommandDataParser {

  private final static Pattern CommandPattern;

  static {
    CommandPattern = Pattern.compile("^%(?<name>[a-z\\.]+)(\\s+)?(?<args>.*)?$",
                                     Pattern.CASE_INSENSITIVE);
  }

  /**
   * Parses text representing an command invocation.
   * @param data the text to parse.
   * @return a parsed CommandData object.
   */
  public CommandData parse(String data) {
    String name = null;
    String args = null;
    String content = null;

    if (data.startsWith("%%")) {
      int newLineIndex = data.indexOf('\n');
      if ((newLineIndex < 0) || (data.length() <= (newLineIndex + 1))) {
        return null;
      }

      content = data.substring(newLineIndex + 1);

      // Strip off everything starting with the first new line. Also strip out a leading '%'
      // so the resulting data matches the syntax of single line commands.
      data = data.substring(1, newLineIndex);
    }

    Matcher matcher = CommandDataParser.CommandPattern.matcher(data);
    if (!matcher.matches()) {
      return null;
    }

    name = matcher.group("name");
    args = matcher.group("args");

    return new CommandData(name, args, content);
  }
}
