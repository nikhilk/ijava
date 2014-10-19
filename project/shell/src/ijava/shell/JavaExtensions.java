// JavaExtensions.java
//

package ijava.shell;

import ijava.*;

/**
 * Standard Java-language related extensions.
 */
public final class JavaExtensions {

  private JavaExtensions() {
  }

  /**
   * Handles %import invocations to add imported packages or types to subsequent compilations.
   */
  public static final class ImportExtension implements EvaluatorExtension {

    @Override
    public Object evaluate(Evaluator evaluator, String declaration, String data) throws Exception {
      JavaShell shell = (JavaShell)evaluator;
      shell.addImport(declaration, /* staticImport */ false);

      return null;
    }
  }
}
