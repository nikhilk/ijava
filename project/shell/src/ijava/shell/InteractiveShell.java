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
public final class InteractiveShell implements Evaluator, SnippetShell {

  private final SnippetDependencies _dependencies;
  private final SnippetRewriter _rewriter;

  private final HashSet<String> _packages;
  private final HashMap<String, byte[]> _types;

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

    // The map containing byte code buffers for declared types keyed by class name.
    _types = new HashMap<String, byte[]>();

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

      if (snippet.getType() == SnippetType.ClassMembers) {
        System.err.println("Only full types and statements are supported at this time.");
        return null;
      }

      _rewriter.rewriteSnippet(snippet);

      SnippetCompiler compiler = new SnippetCompiler(this);
      SnippetCompilation compilation = compiler.compile(snippet);

      if (snippet.getType() == SnippetType.CompilationUnit) {
        // Process the results to record new types, and packages.
        processCompilationUnit(evaluationID, compilation);
      }
      else if (snippet.getType() == SnippetType.CodeBlock) {
        // Process the results to execute the code.
        result = processCodeBlock(evaluationID, compilation);
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
   * @param id the ID to use to generate unique names.
   * @param compilation the result of the code block compilation.
   * @return the result of the code block execution.
   */
  private Object processCodeBlock(int id, SnippetCompilation compilation)
      throws Exception {
    ClassLoader classLoader = new CodeBlockClassLoader(_classLoader, id, compilation.getTypes());

    Class<?> snippetClass = classLoader.loadClass(compilation.getClassName());
    Callable<?> callable = (Callable<?>)snippetClass.newInstance();

    return callable.call();
  }

  /**
   * Process the the results of compiling a compilation unit. This involves two things:
   * - Recording any packages created in the process.
   * - Stashing (or optionally updating) the byte code for types defined, and saving a reference
   *   to the new class loader created to enable loading those types.
   * @param id the ID to use to generate unique names.
   * @param compilation the result of the code block compilation.
   */
  private void processCompilationUnit(int id, SnippetCompilation compilation) {
    for (String packageName : compilation.getPackages()) {
      _packages.add(packageName);
    }

    HashSet<String> newNames = new HashSet<String>();
    for (Map.Entry<String, byte[]> typeEntry : compilation.getTypes().entrySet()) {
      String name = typeEntry.getKey();
      byte[] bytes = typeEntry.getValue();

      byte[] existingBytes = _types.get(name);
      if ((existingBytes != null) && Arrays.equals(existingBytes, bytes)) {
        // Same name, same byte code ... likely the user simply re-executed the same code.
        // Ignore the new class in favor of keeping the old class identity, and increase chances
        // that existing data instances of that class (should they exist) remain valid.

        continue;
      }

      _types.put(name, bytes);
      newNames.add(name);
    }

    if (newNames.size() != 0) {
      // Create a new class loader parented to the current one for the newly defined classes
      _classLoader = new ShellClassLoader(_classLoader, id, newNames);
    }
  }

  /**
   * {@link SnippetShell}
   */
  @Override
  public Set<String> getPackages() {
    return _packages;
  }

  /**
   * {@link SnippetShell}
   */
  @Override
  public Map<String, byte[]> getTypes() {
    return _types;
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
        return _types.get(name);
      }

      return null;
    }
  }

  /**
   * A class loader that allows loading classes generated during compilation from code blocks
   * entered into the shell, while that code is being executed.
   */
  private final class CodeBlockClassLoader extends ByteCodeClassLoader {

    private final Map<String, byte[]> _types;

    /**
     * Initializes an instance of a CodeBlockClassLoader.
     * @param parentClassLoader the parent class loader to chain with.
     * @param id the ID of this class loader.
     * @param types the set of byte code buffers for types keyed by class names.
     */
    public CodeBlockClassLoader(ClassLoader parentClassLoader, int id,
                                Map<String, byte[]> types) {
      super(parentClassLoader, id);
      _types = types;
    }

    @Override
    protected byte[] getByteCode(String name) {
      return _types.get(name);
    }
  }
}
