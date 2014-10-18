// InteractiveShell.java
//

package ijava.shell;

import java.util.*;
import java.util.concurrent.*;

import ijava.*;
import ijava.shell.compiler.*;
import ijava.shell.util.*;

/**
 * Provides the interactive shell or REPL functionality for Java.
 */
public final class InteractiveShell implements Evaluator {

  private final SnippetDependencies _dependencies;
  private final SnippetRewriter _rewriter;

  private final HashSet<String> _packages;
  private final HashMap<String, byte[]> _byteCode;

  private ClassLoader _classLoader;

  /**
   * Initializes an instance of an InteractiveShell.
   */
  public InteractiveShell() {
    _dependencies = new SnippetDependencies();
    _rewriter = new SnippetRewriter(_dependencies);

    _dependencies.addImport("java.io.*", /* staticImport */ false);
    _dependencies.addImport("java.util.*", /* staticImport */ false);

    // The set of declared packages.
    _packages = new HashSet<String>();

    // The map containing all bytecode generated for declared types key'd by full classname.
    _byteCode = new HashMap<String, byte[]>();

    // Default the class loader to the system one initially.
    _classLoader = ClassLoader.getSystemClassLoader();
  }

  /**
   * {@link Evaluator}
   */
  @Override
  public Object evaluate(String data, int evaluationID) throws Exception {
    Object result = null;

    try {
      SnippetParser parser = new SnippetParser();
      Snippet snippet = parser.parse(data, evaluationID);

      System.out.println("Snippet Type: " + snippet.getType());

      _rewriter.rewriteSnippet(snippet);
      System.out.println("----");
      System.out.println(snippet.getRewrittenCode());
      System.out.println("----");

      SnippetCompiler compiler = new SnippetCompiler();
      SnippetCompilation compilation = compiler.compile(snippet, _packages, _byteCode);

      if (snippet.getType() == SnippetType.CompilationUnit) {
        // Process the results to record new types, and packages.
        processCompilationUnit(evaluationID, compilation.getPackages(), compilation.getByteCode());
      }
      else if (snippet.getType() == SnippetType.CodeBlock) {
        // Process the results to execute the code.
        result = processCodeBlock(evaluationID, compilation.getByteCode(), snippet.getClassName());
      }
    }
    catch (SnippetException e) {
      System.err.println(e.getMessage());
    }

    return result;
  }

  /**
   * Process the results of compiling a code block. This involves creating a class loader around
   * the defined class, loading and instantiating that class and executing the code block.
   * @param id
   * @param byteCode
   * @param className
   * @return
   */
  private Object processCodeBlock(int id, Map<String, byte[]> byteCode, String className)
      throws Exception {
    ClassLoader classLoader = new CodeBlockClassLoader(_classLoader, id, byteCode);

    Class<?> snippetClass = classLoader.loadClass(className);
    Callable<?> callable = (Callable<?>)snippetClass.newInstance();

    return callable.call();
  }

  /**
   * Process the the results of compiling a compilation unit. This involves two things:
   * - Recording any packages created in the process.
   * - Stashing (or optionally updating) the byte code for types defined, and saving a reference
   *   to the new class loader created to enable loading those types.
   * @param id the ID of the compilation to create unique names.
   * @param packages the packages produced from the compilation.
   * @param byteCode the byte code produced from the compilation.
   */
  private void processCompilationUnit(int id, Set<String> packages, Map<String, byte[]> byteCode) {
    for (String packageName : packages) {
      _packages.add(packageName);
    }

    HashSet<String> newNames = new HashSet<String>();
    for (Map.Entry<String, byte[]> byteCodeEntry : byteCode.entrySet()) {
      String name = byteCodeEntry.getKey();
      byte[] bytes = byteCodeEntry.getValue();

      byte[] existingBytes = _byteCode.get(name);
      if ((existingBytes != null) && Arrays.equals(existingBytes, bytes)) {
        // Same name, same byte code ... likely the user simply re-executed the same code.
        // Ignore the new class in favor of keeping the old class identity, and increase chances
        // that existing data instances of that class (should they exist) remain valid.

        continue;
      }

      _byteCode.put(name, bytes);
      newNames.add(name);
    }

    if (newNames.size() != 0) {
      // Create a new class loader parented to the current one for the newly defined classes
      _classLoader = new ShellClassLoader(_classLoader, id, newNames);
    }
  }


  /**
   * A class loader that holds on to classes declared within the shell.
   */
  private final class ShellClassLoader extends ByteCodeClassLoader {

    private final HashSet<String> _names;

    /**
     * Initializes an instance of a ShellClassLoader.
     * @param parentClassLoader the parent class loader to chain with.
     * @param id the ID of this class loader.
     * @param names the list of names that should be resolved with this class loader.
     */
    public ShellClassLoader(ClassLoader parentClassLoader, int id, HashSet<String> names) {
      super(parentClassLoader, id);
      _names = names;
    }

    @Override
    protected byte[] getByteCode(String name) {
      if (_names.contains(name)) {
        return _byteCode.get(name);
      }

      return null;
    }
  }

  /**
   * A class loader that allows loading classes generated during compilation from code blocks
   * entered into the shell, while that code is being executed.
   */
  private final class CodeBlockClassLoader extends ByteCodeClassLoader {

    private final Map<String, byte[]> _byteCode;

    /**
     * Initializes an instance of a CodeBlockClassLoader.
     * @param parentClassLoader the parent class loader to chain with.
     * @param id the ID of this class loader.
     * @return the set of byte code buffers keyed by class names.
     */
    public CodeBlockClassLoader(ClassLoader parentClassLoader, int id,
                                Map<String, byte[]> byteCode) {
      super(parentClassLoader, id);
      _byteCode = byteCode;
    }

    @Override
    protected byte[] getByteCode(String name) {
      return _byteCode.get(name);
    }
  }
}
