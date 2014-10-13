// InteractiveShell.java
//

package ijava.shell;

import ijava.*;
import ijava.shell.compiler.*;

/**
 * Provides the interactive shell or REPL functionality for Java.
 */
public final class InteractiveShell implements Evaluator {

  private final SnippetDependencies _dependencies;
  private final SnippetRewriter _rewriter;

  /**
   * Initializes an instance of an InteractiveShell.
   */
  public InteractiveShell() {
    _dependencies = new SnippetDependencies();
    _rewriter = new SnippetRewriter(_dependencies);

    _dependencies.addImport("java.io.*", /* staticImport */ false);
    _dependencies.addImport("java.util.*", /* staticImport */ false);
  }

  /**
   * {@link Evaluator}
   */
  @Override
  public Object evaluate(String data, int evaluationID) throws Exception {
    Snippet snippet = null;

    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(data, evaluationID);

      System.out.println("Snippet Type: " + snippet.getType());

      _rewriter.rewriteSnippet(snippet);
      System.out.println("----");
      System.out.println(snippet.getRewrittenCode());
      System.out.println("----");

      if (snippet.getType() == SnippetType.CodeBlock) {
        SnippetCompiler compiler = new SnippetCompiler();
        SnippetCompilation compilation = compiler.compile(snippet);

        Class<?> snippetClass = compilation.getClassLoader().loadClass(snippet.getClassName());
        Runnable runnable = (Runnable)snippetClass.newInstance();

        runnable.run();
      }
    }
    catch (SnippetException e) {
      System.err.println(e.getMessage());
    }

    return null;
  }
}
