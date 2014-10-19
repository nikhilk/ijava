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

  private final HashSet<String> _imports;
  private final HashSet<String> _staticImports;
  private final HashSet<String> _packages;
  private final HashMap<String, byte[]> _types;

  private ClassLoader _classLoader;
  private String _cachedImports;

  /**
   * Initializes an instance of an InteractiveShell.
   */
  public InteractiveShell() {
    _imports = new HashSet<String>();
    _staticImports = new HashSet<String>();
    _packages = new HashSet<String>();
    _types = new HashMap<String, byte[]>();

    // Import a few packages by default
    _imports.add("java.io.*");
    _imports.add("java.util.*");

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
    }
    catch (SnippetException e) {
      throw new EvaluationError(e.getMessage(), e);
    }

    if (snippet.getType() == SnippetType.ClassMembers) {
      System.err.println("Only full types and statements are supported at this time.");
      return null;
    }

    SnippetRewriter rewriter = new SnippetRewriter(this);
    rewriter.rewrite(snippet);

    SnippetCompiler compiler = new SnippetCompiler(this);
    if (compiler.compile(snippet)) {
      if (snippet.getType() == SnippetType.CompilationUnit) {
        // Process the results to record new types, and packages.
        processCompilationUnit(evaluationID, snippet);
        return null;
      }
      else if (snippet.getType() == SnippetType.CodeBlock) {
        // Process the results to execute the code.
        return processCodeBlock(evaluationID, snippet);
      }

      return result;
    }
    else {
      // Raise an error for compilation errors
      StringBuilder errorBuilder = new StringBuilder();
      for (String error : snippet.getCompilation().getErrors()) {
        errorBuilder.append(error);
        errorBuilder.append("\n");
      }

      throw new EvaluationError(errorBuilder.toString());
    }
  }

  /**
   * Process the results of compiling a code block. This involves creating a class loader around
   * the defined class, loading and instantiating that class and executing the code block.
   * @param id the ID to use to generate unique names.
   * @param snippet the compiled snippet.
   * @return the result of the code block execution.
   */
  private Object processCodeBlock(int id, Snippet snippet)
      throws Exception {
    SnippetCompilation compilation = snippet.getCompilation();
    ClassLoader classLoader = new CodeBlockClassLoader(_classLoader, id, compilation.getTypes());

    Class<?> snippetClass = classLoader.loadClass(snippet.getClassName());
    Callable<?> callable = (Callable<?>)snippetClass.newInstance();

    return callable.call();
  }

  /**
   * Process the the results of compiling a compilation unit. This involves two things:
   * - Recording any packages created in the process.
   * - Stashing (or optionally updating) the byte code for types defined, and saving a reference
   *   to the new class loader created to enable loading those types.
   * @param id the ID to use to generate unique names.
   * @param snippet the compiled snippet.
   */
  private void processCompilationUnit(int id, Snippet snippet) {
    SnippetCompilation compilation = snippet.getCompilation();

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
  public String getImports() {
    if (_cachedImports == null) {
      StringBuilder sb = new StringBuilder();

      for (String s : _imports) {
        sb.append(String.format("import %s;", s));
      }
      for (String s : _staticImports) {
        sb.append(String.format("import static %s;", s));
      }

      _cachedImports = sb.toString();
    }

    return _cachedImports;
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
