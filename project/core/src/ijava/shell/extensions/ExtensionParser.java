// ExtensionParser.java
//

package ijava.shell.extensions;

import java.util.regex.*;

/**
 * Parses extensions to be evaluated in the shell.
 */
public final class ExtensionParser {

  private final static Pattern ExtensionPattern;

  static {
    ExtensionPattern = Pattern.compile("^%(?<name>[a-z]+)(\\s+)?(?<decl>.*)?$",
                                       Pattern.CASE_INSENSITIVE);
  }

  /**
   * Parses an extension object from data representing an extension invocation.
   * @param data the extension data.
   * @return a parsed Extension object.
   */
  public Extension parse(String data) {
    String name = null;
    String declaration = null;
    String content = null;

    if (data.startsWith("%%")) {
      int newLineIndex = data.indexOf('\n');
      if ((newLineIndex < 0) || (data.length() <= (newLineIndex + 1))) {
        return null;
      }

      content = data.substring(newLineIndex + 1);

      // Strip off everything starting with the first new line. Also strip out a leading '%'
      // so the resulting data matches the syntax of single line extensions.
      data = data.substring(1, newLineIndex);
    }

    Matcher matcher = ExtensionParser.ExtensionPattern.matcher(data);
    if (!matcher.matches()) {
      return null;
    }

    name = matcher.group("name");
    declaration = matcher.group("decl");

    return new Extension(name, declaration, content);
  }
}
