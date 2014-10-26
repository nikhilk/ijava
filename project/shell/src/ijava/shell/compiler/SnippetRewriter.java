// SnippetRewriter.java
//

package ijava.shell.compiler;

import java.util.*;

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

    if (snippet.getType() == SnippetType.ClassMembers) {
      rewrittenCode = rewriteClassMembers(snippet.getClassName(),
                                          snippet.getCode(),
                                          snippet.getClassMembers());
    }
    else if (snippet.getType() == SnippetType.CodeBlock) {
      rewrittenCode = rewriteCodeBlock(snippet.getClassName(), snippet.getCode());
    }

    snippet.setRewrittenCode(rewrittenCode);
  }

  private String rewriteClassMembers(String className, String code,
                                     List<SnippetCodeMember> members) {
    // TODO: Temporary implementation
    for (SnippetCodeMember member: members) {
      System.out.println("name: " + member.getName());

      if (member.isField()) {
        System.out.println("type: " + member.getType());
      }
      else {
        System.out.println("type: " + member.getCode());
      }
      System.out.println();
    }
    System.out.println("---");

    StringBuilder sb = new StringBuilder();

    sb.append(_shell.getImports());

    sb.append("public class ");
    sb.append(className);
    sb.append(" implements java.util.concurrent.Callable<Object> {");
    sb.append(" @Override public Object call() throws Exception { return new Delta(); } ");
    sb.append(" public class Delta { ");
    sb.append(code);
    sb.append(" }}");

    String rewrittenCode = sb.toString();
    System.out.println(rewrittenCode);

    return rewrittenCode;
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
