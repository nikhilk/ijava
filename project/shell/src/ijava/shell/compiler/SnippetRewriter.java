// SnippetRewriter.java
//

package ijava.shell.compiler;

/**
 * Rewrites snippet code so it is a compilable, well-formed compilation unit.
 */
public final class SnippetRewriter {

  private final SnippetShell _shell;

  /**
   * Initializes an instance of a SnippetRewriter with the shell that is performing the
   * rewriting.
   * @param shell the shell performing the rewriting.
   */
  public SnippetRewriter(SnippetShell shell) {
    _shell = shell;
  }

  /**
   * Rewrites snippet code so it can be compiled.
   * @param snippet the snippet to be rewritten.
   * @return the rewritten snippet code.
   */
  public void rewrite(Snippet snippet) {
    String rewrittenCode = null;

    if (snippet.getType() == SnippetType.CodeBlock) {
      rewrittenCode = rewriteCodeBlock(snippet.getClassName(), snippet.getCode());
    }

    // TODO: Rewrite compilation units (to add imports) and class members

    snippet.setRewrittenCode(rewrittenCode);
  }

  private String rewriteCodeBlock(String className, String codeBlock) {
    StringBuilder sb = new StringBuilder();

    sb.append(_shell.getImports());

    // Wrap the code block into a call method on a class implementing Callable<Object>.
    // The method body ends with a return statement so there is guaranteed to be a return value.
    //
    // The code block is also placed within an if (true) { ... } so that the added return
    // statement doesn't result in a compile error about unreachable code.

    sb.append("public class ");
    sb.append(className);
    sb.append(" implements java.util.concurrent.Callable<Object> { ");
    sb.append("@Override public Object call() throws Exception { ");
    sb.append("if (true) { ");
    sb.append(codeBlock);
    sb.append(" } return null; }}");

    return sb.toString();
  }
}
