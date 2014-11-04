// SnippetImport.java
//

package ijava.shell.compiler;

/**
 * Represents an import declaration within a snippet.
 */
public final class SnippetImport {

  private final String _name;
  private final boolean _static;

  /**
   * Initializes an instance of a SnippetImport.
   * @param name the name being imported.
   * @param isStatic whether the import is qualified as a static import.
   */
  public SnippetImport(String name, boolean isStatic) {
    _name = name;
    _static = isStatic;
  }

  /**
   * Gets the name being imported.
   * @return the referenced name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Whether the import is a static import or not.
   * @return true if the import is a static import.
   */
  public boolean isStatic() {
    return _static;
  }
}
