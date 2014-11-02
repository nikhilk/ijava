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

  private List<SnippetCodeMember> _members;

  private String _rewrittenCode;
  private SnippetCompilation _compilation;

  /**
   * Creates an instance of a Snippet from its code and type.
   * @param type the type of the snippet inferred from the code.
   * @param code the code represented by the snippet.
   * @param className the name of the top-level class.
   */
  private Snippet(SnippetType type, String code, String className) {
    _code = code;
    _type = type;
    _className = className;
  }

  /**
   * Creates a code snippet representing one or more class members.
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @param classMembers the list of members declared in the code.
   * @return a Snippet object.
   */
  public static Snippet classMembers(String code, String className,
                                     List<SnippetCodeMember> members) {
    Snippet snippet = new Snippet(SnippetType.ClassMembers, code, className);
    snippet._members = members;

    return snippet;
  }

  /**
   * Creates a code snippet representing an executable block of statements.
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @return a Snippet object.
   */
  public static Snippet codeBlock(String code, String className) {
    return new Snippet(SnippetType.CodeBlock, code, className);
  }

  /**
   * Creates a code snippet representing an executable expression.
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @return a Snippet object.
   */
  public static Snippet codeExpression(String code, String className) {
    return new Snippet(SnippetType.CodeExpression, code, className);
  }

  /**
   * Creates a code snippet representing a complete compilation unit (or java file).
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @return a Snippet object.
   */
  public static Snippet compilationUnit(String code, String className) {
    return new Snippet(SnippetType.CompilationUnit, code, className);
  }

  /**
   * Gets the list of class members in this code snippet. Only valid for snippets that represent
   * class members.
   * @return the list of member names.
   */
  public List<SnippetCodeMember> getClassMembers() {
    assert _type == SnippetType.ClassMembers : "Only applicable to CompilationUnit snippets.";
    return _members;
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
