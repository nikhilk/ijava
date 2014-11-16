// JavaRewriter.java
//

package ijava.shell;

import java.util.*;
import ijava.shell.compiler.*;

/**
 * Rewrites snippet code so it is a compilable, well-formed compilation unit.
 */
public final class JavaRewriter {

  private final InteractiveShell _shell;

  /**
   * Initializes an instance of a SnippetRewriter with the shell that is performing the
   * rewriting.
   * @param shell the shell performing the rewriting.
   */
  public JavaRewriter(InteractiveShell shell) {
    _shell = shell;
  }

  /**
   * Rewrites snippet code so it can be compiled.
   * @param snippet the snippet to be rewritten.
   * @return the rewritten snippet code.
   */
  public String rewrite(Snippet snippet) {
    String rewrittenCode = null;

    switch (snippet.getType()) {
      case CodeMembers:
        rewrittenCode = rewriteCodeMembers(snippet.getClassName(),
                                           snippet.getCode(),
                                           snippet.getCodeMembers());
        break;
      case CodeBlock:
        rewrittenCode = rewriteCodeBlock(snippet.getClassName(), snippet.getCode());
        break;
      case CodeExpression:
        rewrittenCode = rewriteCodeExpression(snippet.getClassName(), snippet.getCode());
        break;
      default:
        break;
    }

    return rewrittenCode;
  }

  private String rewriteCodeMembers(String className, String code,
                                    List<SnippetCodeMember> members) {
    // The rewritten code is a class that can be compiled and executed.
    // - It contains an inner class called __Code that contains the class members
    //   being declared.
    // - The generated class implements Callable, and the implementation of Call
    //   instantiates and returns an instance of the inner __Code class.
    // - The generated class also contains all fields and methods being tracked
    //   within the shell so they are accessible to the new class members.
    // - The new code is placed in an inner class, so that the outer class can be
    //   instantiated and initialized with previous state for field values, before
    //   the inner class is instantiated and new code within it is executed.

    StringBuilder sb = new StringBuilder();

    sb.append(_shell.getImports());

    sb.append("public class ");
    sb.append(className);
    sb.append(" implements java.util.concurrent.Callable<Object> {");
    sb.append(" public class __Inner { ");
    sb.append(code);
    sb.append("\n");
    sb.append("public __Inner() throws Exception { }\n");
    sb.append(" }\n\n");
    sb.append(_shell.getState().getCode());
    sb.append("  @Override public Object call() throws Exception {\n");
    sb.append("    return new __Inner();\n");
    sb.append("  }\n");
    sb.append("}");

    return sb.toString();
  }

  private String rewriteCodeBlock(String className, String codeBlock) {
    // The rewritten code is a class that can be compiled and executed.
    // - The class implements Callable, and includes user code as the implementation of Call.
    // - The return value is either the result of a return statement, or a fallback null
    //   value (so there is a guaranteed return statement within the Call method body.
    // - The user code is wrapped in an if (true) block, so that the if the user code has
    //   a throw statement, the generated return null statement doesn't trigger a compiler
    //   error about being unreachable.
    // - The class also contains declarations for all fields and methods being tracked
    //   within the shell, so they are accessible to the new code block.

    StringBuilder sb = new StringBuilder();

    sb.append(_shell.getImports());
    sb.append("public class ");
    sb.append(className);
    sb.append(" implements java.util.concurrent.Callable<Object> { ");
    sb.append(" @Override public Object call() throws Exception { ");
    sb.append("if (true) { ");
    sb.append(codeBlock);
    sb.append(" } return null; }\n\n");
    sb.append(_shell.getState().getCode());
    sb.append("}");

    return sb.toString();
  }

  private String rewriteCodeExpression(String className, String codeExpression) {
    // Mostly similar to a code block, once the expression has been converted to a statement.

    String codeBlock = "return (" + codeExpression + ");";
    return rewriteCodeBlock(className, codeBlock);
  }
}
