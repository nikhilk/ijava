// SnippetCompilation.java
//

package ijava.shell.compiler;

/**
 * Represents the results of a snippet compilation.
 */
public final class SnippetCompilation {

  private final ClassLoader _classLoader;

  /**
   * Initializes a SnippetCompilation instance.
   * @param classLoader the resulting class loader.
   */
  public SnippetCompilation(ClassLoader classLoader) {
    _classLoader = classLoader;
  }

  /**
   * Gets the class loader resulting from the compilation.
   * @return the class loader containing compiled types.
   */
  public ClassLoader getClassLoader() {
    return _classLoader;
  }
}
