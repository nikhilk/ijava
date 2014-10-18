// SnippetCompilation.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Represents the results of a snippet compilation.
 */
public final class SnippetCompilation {

  private final Set<String> _packages;
  private final Map<String, byte[]> _byteCode;

  /**
   * Initializes a SnippetCompilation instance.
   * @param packages the resulting set of package names (if any).
   * @param byteCode the resulting set of byte code buffers keyed by class names.
   */
  public SnippetCompilation(Set<String> packages, Map<String, byte[]> byteCode) {
    _packages = packages;
    _byteCode = byteCode;
  }

  /**
   * Gets the byte code resulting from the compilation.
   * @return the set of byte code buffers keyed by class names.
   */
  public Map<String, byte[]> getByteCode() {
    return _byteCode;
  }

  /**
   * Gets the resulting set of packages defined in the compilation.
   * @return the set of package names.
   */
  public Set<String> getPackages() {
    return _packages;
  }
}
