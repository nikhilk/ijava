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

    sb.append("public class ");
    sb.append(className);
    sb.append(" implements Runnable {\n");
    sb.append("  @Override public void run() {\n");
    sb.append(codeBlock);
    sb.append("\n");
    sb.append("  }\n");
    sb.append("}\n");

    return sb.toString();
  }
}
