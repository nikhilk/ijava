// SnippetDependencies.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Manages snippet dependencies, including relevant imports.
 */
public final class SnippetDependencies {

  private final HashSet<String> _imports;
  private final HashSet<String> _staticImports;

  private String _importCode;

  /**
   * Initializes an instance of a DependencyManager.
   */
  public SnippetDependencies() {
    _imports = new HashSet<String>();
    _staticImports = new HashSet<String>();
  }

  /**
   * Gets the list of imports that have been added.
   * @return the list of imports.
   */
  public String[] getImports() {
    return _imports.toArray(new String[_imports.size()]);
  }

  /**
   * Gets the list of static imports that have been added.
   * @return the list of static imports.
   */
  public String[] getStaticImports() {
    return _staticImports.toArray(new String[_staticImports.size()]);
  }

  /**
   * Adds the specified import to the list of imports.
   * @param name the name of the imported package or class.
   * @param staticImport whether the import should be a static import.
   */
  public void addImport(String name, boolean staticImport) {
    if (staticImport) {
      _staticImports.add(name);
    }
    else {
      _imports.add(name);
    }

    // Invalidate the cached import code.
    _importCode = null;
  }

  /**
   * Gets the code snippet representing the set of the packages to be imported.
   * @return the current imports.
   */
  public String toCode() {
    if (_importCode == null) {
      StringBuilder sb = new StringBuilder();

      for (String s : _imports) {
        sb.append(String.format("import %s;\n", s));
      }
      for (String s : _staticImports) {
        sb.append(String.format("import static %s;\n", s));
      }

      _importCode = sb.toString();
    }

    return _importCode;
  }
}
