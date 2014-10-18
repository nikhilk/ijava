// SnippetShell.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Encapsulates the state managed by the shell, and consumed during compilation.
 */
public interface SnippetShell {

  /**
   * Gets the list of imports.
   * @return the list of imports as formatted code.
   */
  public String getImports();

  /**
   * Gets the set of packages declared in previously processed packages.
   * @return the set of declared packages.
   */
  public Set<String> getPackages();

  /**
   * Gets the set of byte buffers representing byte code for types declared in previously
   * processed snippets.
   * @return the set of byte code buffers keyed by class name.
   */
  public Map<String, byte[]> getTypes();
}
