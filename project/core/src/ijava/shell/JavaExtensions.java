// JavaExtensions.java
//

package ijava.shell;

import java.net.*;

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
   * Handles %import invocations to add imported packages or types to subsequent compilations.
   */
  public static final class ImportExtension implements Extension {

    @Override
    public Object evaluate(JavaShell shell, String declaration, String data) throws Exception {
      shell.addImport(declaration, /* staticImport */ false);
      return null;
    }
  }
}
