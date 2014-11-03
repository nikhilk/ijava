// SnippetType.java

package ijava.shell.compiler;

/**
 * Indicates the type of snippet based on snippet parsing.
 */
public enum SnippetType {

  /**
   * Represents a top-level (complete) compilation unit consisting
   * of type declarations.
   */
  CompilationUnit,

  /**
   * Represents one or a set of fields and/or method declarations.
   */
  CodeMembers,

  /**
   * Represents one or more statements that should be executed.
   */
  CodeBlock,

  /**
   * Represents a single expression that should be executed.
   */
  CodeExpression
}
