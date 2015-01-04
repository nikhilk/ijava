// SnippetException.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Encapsulates error information related to snippet code blocks.
 * 
 * TODO: Capture line/column position as well.
 */
@SuppressWarnings("serial")
public final class SnippetException extends Exception {

  /**
   * Creates an instance of a SnippetException.
   * @param error the error message.
   */
  public SnippetException(String error) {
    super(error);
  }

  /**
   * Creates an instance of a SnippetException.
   * @param errors the list of error messages.
   */
  public SnippetException(List<String> errors) {
    super(SnippetException.createError(errors));
  }

  private static String createError(List<String> errors) {
    StringBuilder sb = new StringBuilder();
    for (String error : errors) {
      sb.append(error);
      sb.append('\n');
    }

    return sb.toString();
  }
}
