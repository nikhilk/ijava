// JavaExtensions.java
//

package ijava.shell;

import java.net.*;
import java.util.*;

/**
 * Standard Java-language related extensions.
 */
public final class JavaExtensions {

  private JavaExtensions() {
  }

  /**
   * Handles %dependency invocations to add dependencies to modules to subsequent compilations.
   */
  public static final class DependencyExtension implements Extension {

    @Override
    public Object evaluate(JavaShell shell, String declaration, String data) throws Exception {
      shell.addDependency(URI.create(declaration));
      return null;
    }
  }

  /**
   * Handles %imports invocations to list the current set of imports.
   */
  public static final class ImportsExtension implements Extension {

    @Override
    public Object evaluate(JavaShell shell, String declaration, String data) throws Exception {
      String[] imports = shell.getImports().split(";");
      Arrays.sort(imports);

      StringBuilder sb = new StringBuilder();
      for (String s: imports) {
        sb.append(s);
        sb.append(";\n");
      }

      return sb.toString();
    }
  }
}
