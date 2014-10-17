// SnippetCompilation.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Represents the results of a snippet compilation.
 */
public final class SnippetCompilation {

  private final Map<String, byte[]> _byteCode;

  /**
   * Initializes a SnippetCompilation instance.
   * @param byteCode the resulting set of byte code buffers keyed by class names.
   */
  public SnippetCompilation(Map<String, byte[]> byteCode) {
    _byteCode = byteCode;
  }

  /**
   * Gets the byte code resulting from the compilation.
   * @return the set of byte code buffers keyed by class names.
   */
  public Map<String, byte[]> getByteCode() {
    return _byteCode;
  }
}
