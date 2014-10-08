// InteractiveShell.java
//

package ijava.shell;

import ijava.*;
import ijava.shell.compiler.*;

public final class InteractiveShell implements Evaluator {

  @Override
  public Object evaluate(String data) throws Exception {
    Snippet snippet = null;

    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(data);

      return snippet.getType().toString();
    }
    catch (SnippetException e) {
      System.err.println(e.getMessage());
      return null;
    }
  }
}
