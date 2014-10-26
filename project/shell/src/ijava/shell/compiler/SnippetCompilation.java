// SnippetCompilation.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Represents the results of a snippet compilation.
 */
public final class SnippetCompilation {

  private final Set<String> _packages;
  private final Map<String, byte[]> _types;
  private final List<String> _errors;

  /**
   * Initializes a SnippetCompilation instance.
   * @param packages the resulting set of package names (if any).
   * @param types the resulting set of types.
   * @param errors the resulting set of errors.
   */
  public SnippetCompilation(Set<String> packages, Map<String, byte[]> types, List<String> errors) {
    _packages = packages;
    _types = types;
    _errors = errors;
  }

  /**
   * Gets the list of errors resulting from compilation.
   * @return the list of errors if there were any.
   */
  public List<String> getErrors() {
    return _errors;
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

  /**
   * Gets whether there are any errors as a result of compilation.
   * @return true if there was an error; false otherwise.
   */
  public boolean hasErrors() {
    return (_errors != null) && (_errors.size() != 0);
  }
}
