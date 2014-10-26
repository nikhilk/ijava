// JavaShell.java
//

package ijava.shell;

import java.util.*;
import java.util.concurrent.*;
import ijava.*;
import ijava.shell.compiler.*;
import ijava.shell.extensions.*;
import ijava.shell.util.*;

/**
 * Provides the interactive shell or REPL functionality for Java.
 */
public final class JavaShell implements Evaluator, SnippetShell {

  private final HashMap<String, EvaluatorExtension> _extensions;

  private final HashSet<String> _imports;
  private final HashSet<String> _staticImports;
  private final HashSet<String> _packages;
  private final HashMap<String, byte[]> _types;

  private ClassLoader _classLoader;
  private String _cachedImports;

  /**
   * Initializes an instance of an JavaShell.
   */
  public JavaShell() {
    _extensions = new HashMap<String, EvaluatorExtension>();

    _imports = new HashSet<String>();
    _staticImports = new HashSet<String>();
    _packages = new HashSet<String>();
    _types = new HashMap<String, byte[]>();

    // Default the class loader to the system one initially.
    _classLoader = ClassLoader.getSystemClassLoader();

    // Import a few packages by default
    addImport("java.io.*", /* staticImport */ false);
    addImport("java.util.*", /* staticImport */ false);
    addImport("java.net.*", /* staticImport */ false);

    // Register a few extensions by default
    registerExtension("import", new JavaExtensions.ImportExtension());
  }

  /**
   * Adds a package to be imported for subsequent compilations.
   * @param importName the package or type to be imported.
   * @param staticImport whether the import should be a static import of a type.
   */
  public void addImport(String importName, boolean staticImport) {
    if (staticImport) {
      _staticImports.add(importName);
    }
    else {
      _imports.add(importName);
    }

    _cachedImports = null;
  }

  /**
   * Invokes an extension for the specified evaluation.
   * @param data the evaluation text.
   */
  private Object invokeExtension(String data) throws Exception {
    ExtensionParser parser = new ExtensionParser();
    Extension extensionData = parser.parse(data);

    if (extensionData == null) {
      throw new EvaluationError("Invalid syntax.");
    }

    String name = extensionData.getName();
    EvaluatorExtension extension = _extensions.get(name);
    if (extension == null) {
      throw new EvaluationError("Invalid syntax. Unknown identifier '" + name + "'");
    }

    return extension.evaluate(this, extensionData.getDeclaration(), extensionData.getContent());
  }

  /**
   * Process the results of compiling class members.
   * @param id the ID to use to generate unique names.
   * @param snippet the compiled snippet.
   */
  private void processClassMembers(int id, Snippet snippet) throws Exception {
    // TODO: Temporary implementation
    SnippetCompilation compilation = snippet.getCompilation();
    ClassLoader classLoader = new CodeBlockClassLoader(_classLoader, id, compilation.getTypes());

    Class<?> snippetClass = classLoader.loadClass(snippet.getClassName());
    snippetClass.newInstance();
  }

  /**
   * Process the results of compiling a code block. This involves creating a class loader around
   * the defined class, loading and instantiating that class and executing the code block.
   * @param id the ID to use to generate unique names.
   * @param snippet the compiled snippet.
   * @return the result of the code block execution.
   */
  private Object processCodeBlock(int id, Snippet snippet) throws Exception {
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
   * Registers an extension so it may be invoked.
   * @param name the name of the extension used in invoking it.
   * @param extension the extension to be registered.
   */
  public void registerExtension(String name, EvaluatorExtension extension) {
    _extensions.put(name, extension);
  }

  /**
   * {@link Evaluator}
   */
  @Override
  public Object evaluate(String data, int evaluationID) throws Exception {
    if (data.startsWith("%")) {
      return invokeExtension(data);
    }

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(data, evaluationID);
    }
    catch (SnippetException e) {
      throw new EvaluationError(e.getMessage(), e);
    }

    JavaRewriter rewriter = new JavaRewriter(this);
    snippet.setRewrittenCode(rewriter.rewrite(snippet));

    SnippetCompiler compiler = new SnippetCompiler(this);
    SnippetCompilation compilation = compiler.compile(snippet);

    if (!compilation.hasErrors()) {
      snippet.setCompilation(compilation);

      Object result = null;
      switch (snippet.getType()) {
        case CompilationUnit:
          processCompilationUnit(evaluationID, snippet);
          break;
        case ClassMembers:
          processClassMembers(evaluationID, snippet);
          break;
        case CodeBlock:
          result = processCodeBlock(evaluationID, snippet);
          break;
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
