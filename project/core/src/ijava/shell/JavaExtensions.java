// JavaExtensions.java
//

package ijava.shell;

/**
 * Standard Java-language related extensions.
 */
public final class JavaExtensions {

  private JavaExtensions() {
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
