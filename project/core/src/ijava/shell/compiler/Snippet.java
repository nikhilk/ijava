// Snippet.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Represents a code snippet along with metadata about the snippet.
 */
public final class Snippet {

  private final SnippetType _type;
  private final String _code;
  private final String _className;
  private final String _packageName;

  private List<SnippetImport> _imports;
  private List<SnippetCodeMember> _members;

  private String _rewrittenCode;
  private SnippetCompilation _compilation;

  /**
   * Creates an instance of a Snippet from its code and type.
   * @param type the type of the snippet inferred from the code.
   * @param code the code represented by the snippet.
   * @param className the name of the top-level class.
   * @param packageName the name of the package if one was declared.
   */
  private Snippet(SnippetType type, String code, String className, String packageName) {
    _code = code;
    _type = type;
    _className = className;
    _packageName = packageName;
  }

  /**
   * Creates a code snippet representing an executable block of statements.
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @return a Snippet object.
   */
  public static Snippet codeBlock(String code, String className) {
    return new Snippet(SnippetType.CodeBlock, code, className, /* packageName */ null);
  }

  /**
   * Creates a code snippet representing an executable expression.
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @return a Snippet object.
   */
  public static Snippet codeExpression(String code, String className) {
    return new Snippet(SnippetType.CodeExpression, code, className, /* packageName */ null);
  }

  /**
   * Creates a code snippet representing one or more class members.
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @param members the list of members declared in the code.
   * @return a Snippet object.
   */
  public static Snippet codeMembers(String code, String className,
                                    List<SnippetCodeMember> members) {
    Snippet snippet = new Snippet(SnippetType.CodeMembers, code, className, /* packageName */ null);
    snippet._members = members;

    return snippet;
  }

  /**
   * Creates a code snippet representing a set of import declarations.
   * @param imports the list of imports.
   * @return a Snippet object.
   */
  public static Snippet compilationImports(List<SnippetImport> imports) {
    Snippet snippet = new Snippet(SnippetType.CompilationImports, "", "", null);
    snippet._imports = imports;

    return snippet;
  }

  /**
   * Creates a code snippet representing a complete compilation unit (or java file).
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @param packageName the name of the package if one was declared.
   * @return a Snippet object.
   */
  public static Snippet compilationUnit(String code, String className, String packageName) {
    return new Snippet(SnippetType.CompilationUnit, code, className, packageName);
  }

  /**
   * Gets the name of the top-level class in this code snippet. Only valid for snippets that
   * represent compilation units.
   * @return the name of the top-level class.
   */
  public String getClassName() {
    return _className;
  }

  /**
   * Gets the code used to parse and create this snippet.
   * @return the code of the snippet.
   */
  public String getCode() {
    return _code;
  }

  /**
   * Gets the list of class members in this code snippet.
   * @return the list of members.
   */
  public List<SnippetCodeMember> getCodeMembers() {
    assert _type == SnippetType.CodeMembers : "Only applicable to CodeMembers snippets.";
    return _members;
  }

  /**
   * Gets the compilation result from compiling the snippet code.
   * @return the compilation result.
   */
  public SnippetCompilation getCompilation() {
    return _compilation;
  }

  /**
   * Sets the compilation result from compiling the snippet code.
   * @param value the compilation result.
   */
  public void setCompilation(SnippetCompilation value) {
    _compilation = value;
  }

  /**
   * Gets the list of import declarations in the snippet.
   * @return the list of referenced names.
   */
  public List<SnippetImport> getImports() {
    assert _type == SnippetType.CompilationImports : "Only applicable to import snippets.";
    return _imports;
  }

  /**
   * Gets the name of the package if one was declared within a compilation unit.
   * @return the name of the package.
   */
  public String getPackageName() {
    assert _type == SnippetType.CompilationUnit : "Only applicable to compilation units.";
    return _packageName;
  }

  /**
   * Gets the rewritten version of the snippet code.
   * @return the compilable snippet code.
   */
  public String getRewrittenCode() {
    if (_rewrittenCode != null) {
      return _rewrittenCode;
    }
    else {
      return getCode();
    }
  }

  /**
   * Sets the rewritten version of the snippet code.
   * @param value the compilable rewritten snippet code.
   */
  public void setRewrittenCode(String value) {
    _rewrittenCode = value;
  }

  /**
   * Gets the snippet type based on inference over the associated code.
   * @return the snippet type.
   */
  public SnippetType getType() {
    return _type;
  }
}
