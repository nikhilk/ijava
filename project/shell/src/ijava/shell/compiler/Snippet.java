// Snippet.java
//

package ijava.shell.compiler;

import java.util.*;

/**
 * Represents a code snippet along with metadata about the snippet.
 */
public final class Snippet {

  private final String _code;
  private final SnippetType _type;

  private String _className;
  private Map<String, Object> _classMembers;

  /**
   * Creates an instance of a Snippet from its code and type.
   * @param code the code represented by the snippet.
   * @param type the type of the snippet inferred from the code.
   */
  private Snippet(String code, SnippetType type) {
    _code = code;
    _type = type;
  }

  /**
   * Creates a code snippet representing one or more class members.
   * @param code the code representing the snippet.
   * @param classMembers the list of members declared in the code.
   * @return a Snippet object.
   */
  public static Snippet classMembers(String code, Map<String, Object> classMembers) {
    Snippet snippet = new Snippet(code, SnippetType.ClassMembers);
    snippet._classMembers = classMembers;

    return snippet;
  }

  /**
   * Creates a code snippet representing an executable block of statements.
   * @param code the code representing the snippet.
   * @param parseContext optional parser related information.
   * @return a Snippet object.
   */
  public static Snippet codeBlock(String code) {
    return new Snippet(code, SnippetType.CodeBlock);
  }

  /**
   * Creates a code snippet representing a complete compilation unit (or java file).
   * @param code the code representing the snippet.
   * @param className the name of the top-level class.
   * @return a Snippet object.
   */
  public static Snippet compilationUnit(String code, String className) {
    Snippet snippet = new Snippet(code, SnippetType.CompilationUnit);
    snippet._className = className;

    return snippet;
  }

  /**
   * Gets the list of class members in this code snippet. Only valid for snippets that represent
   * class members.
   * @return the list of member names.
   */
  public Map<String, Object> getClassMembers() {
    assert _type == SnippetType.ClassMembers : "Only applicable to CompilationUnit snippets.";
    return _classMembers;
  }

  /**
   * Gets the name of the top-level class in this code snippet. Only valid for snippets that
   * represent compilation units.
   * @return the name of the top-level class.
   */
  public String getClassName() {
    assert _type == SnippetType.CompilationUnit : "Only applicable to CompilationUnit snippets.";
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
   * Gets the snippet type based on inference over the associated code.
   * @return the snippet type.
   */
  public SnippetType getType() {
    return _type;
  }
}
