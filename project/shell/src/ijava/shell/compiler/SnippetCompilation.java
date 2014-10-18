// SnippetCompilation.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Represents the results of a snippet compilation.
 */
public final class SnippetCompilation {

  private final String _className;
  private final Set<String> _packages;
  private final Map<String, byte[]> _types;

  /**
   * Initializes a SnippetCompilation instance.
   * @param className the name of the primary class in the compilation.
   * @param packages the resulting set of package names (if any).
   * @param types the resulting set of types.
   */
  public SnippetCompilation(String className, Set<String> packages, Map<String, byte[]> types) {
    _className = className;
    _packages = packages;
    _types = types;
  }

  /**
   * Gets the class name of the primary class in the compilation.
   * @return
   */
  public String getClassName() {
    return _className;
  }

  /**
   * Gets the set of types resulting from compilation.
   * @return a set of byte buffers representing byte code keyed by class name.
   */
  public Map<String, byte[]> getTypes() {
    return _types;
  }

  /**
   * Gets the set of packages resulting from compilation.
   * @return a set of declared package names.
   */
  public Set<String> getPackages() {
    return _packages;
  }
}
