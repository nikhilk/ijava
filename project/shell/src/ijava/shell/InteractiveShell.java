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
    Snippet snippet = null;

    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(data, evaluationID);

      System.out.println("Snippet Type: " + snippet.getType());

      _rewriter.rewriteSnippet(snippet);
      System.out.println("----");
      System.out.println(snippet.getRewrittenCode());
      System.out.println("----");

      SnippetCompiler compiler = new SnippetCompiler();
      SnippetCompilation compilation = compiler.compile(snippet, _packages, _byteCode);

      if (snippet.getType() == SnippetType.CompilationUnit) {
        // Process the resulting byte code for any new or updated types declared in the snippet.
        processCompilationResults(evaluationID,
                                  compilation.getPackages(),
                                  compilation.getByteCode());
      }
      else if (snippet.getType() == SnippetType.CodeBlock) {
        // Load the class created for the code block, and execute it.
        ClassLoader classLoader =
            new CodeBlockClassLoader(_classLoader, evaluationID, compilation.getByteCode());

        Class<?> snippetClass = classLoader.loadClass(snippet.getClassName());
        Callable<?> callable = (Callable<?>)snippetClass.newInstance();

        result = callable.call();
      }
    }
    catch (SnippetException e) {
      System.err.println(e.getMessage());
    }

    return result;
  }

  /**
   * Gets the byte code for the specified class name.
   * @param className the class to lookup.
   * @return the bytes forming the class; null if it wasn't found.
   */
  public byte[] getByteCode(String className) {
    return _byteCode.get(className);
  }

  /**
   * Process the bytecode resulting from a compilation to add any new types resulting from the
   * compilation. If there are new types, a new class loader is created.
   * @param id the ID of the compilation to create unique names.
   * @param packages the packages produced from the compilation.
   * @param byteCode the byte code produced from the compilation.
   */
  private void processCompilationResults(int id,
                                         Set<String> packages,
                                         Map<String, byte[]> byteCode) {
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
        return InteractiveShell.this.getByteCode(name);
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
