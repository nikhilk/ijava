// SnippetRewriter.java
//

package ijava.shell.compiler;

/**
 * Rewrites snippet code so it is a compilable, well-formed compilation unit.
 */
public final class SnippetRewriter {

  private final SnippetDependencies _dependencies;

  /**
   * Initializes an instance of a SnippetRewriter.
   * @param dependencies the dependencies to reference during rewriting.
   */
  public SnippetRewriter(SnippetDependencies dependencies) {
    _dependencies = dependencies;
  }

  /**
   * Rewrites snippet code so it can be compiled.
   * @param snippet the snippet to be rewritten.
   * @return the rewritten snippet code.
   */
  public void rewriteSnippet(Snippet snippet) {
    String rewrittenCode = null;

    if (snippet.getType() == SnippetType.CodeBlock) {
      rewrittenCode = rewriteCodeBlock(snippet.getClassName(), snippet.getCode());
    }

    snippet.setRewrittenCode(rewrittenCode);
  }

  private String rewriteCodeBlock(String className, String codeBlock) {
    StringBuilder sb = new StringBuilder();

    sb.append(_dependencies.toCode());
    sb.append('\n');

    // Wrap the code block into a call method on a class implementing Callable<Object>.
    // The method body ends with a return statement so there is guaranteed to be a return value.
    //
    // The code block is also placed within an if (true) { ... } so that the added return
    // statement doesn't result in a compile error about unreachable code.

    sb.append("public class ");
    sb.append(className);
    sb.append(" implements java.util.concurrent.Callable<Object> {\n");
    sb.append("  @Override public Object call() throws Exception {\n");
    sb.append("    if (true) {\n");
    sb.append(codeBlock);
    sb.append("\n");
    sb.append("    }\n");
    sb.append("    return null;\n");
    sb.append("  }\n");
    sb.append("}\n");

    return sb.toString();
  }
}
