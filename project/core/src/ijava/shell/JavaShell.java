// JavaShell.java
//

package ijava.shell;

import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import ijava.*;
import ijava.shell.compiler.*;

/**
 * Provides the interactive shell or REPL functionality for Java.
 */
public final class JavaShell implements Evaluator {

  private final static String ERROR_TYPE_REDECLARED =
      "The type of the variable '%s', '%s', has changed, and its value is no longer usable.\n" +
          "Please run the code to re-initialize that variable first, or simply re-run this code " +
          "to ignore the error and discard it.";

  private final HashMap<String, Extension> _extensions;
  private final HashMap<String, DependencyResolver> _resolvers;

  private final HashMap<String, Dependency> _dependencies;
  private final HashSet<String> _jars;
  private final HashSet<String> _imports;
  private final HashSet<String> _staticImports;
  private final HashSet<String> _packages;
  private final HashMap<String, byte[]> _types;

  private final JavaShellState _state;

  private ClassLoader _classLoader;
  private String _cachedImports;

  /**
   * Initializes an instance of an JavaShell.
   */
  public JavaShell() {
    _extensions = new HashMap<String, Extension>();
    _resolvers = new HashMap<String, DependencyResolver>();

    _dependencies = new HashMap<String, Dependency>();
    _jars = new HashSet<String>();
    _imports = new HashSet<String>();
    _staticImports = new HashSet<String>();
    _packages = new HashSet<String>();
    _types = new HashMap<String, byte[]>();

    // Register a few extensions by default
    registerExtension("dependency", new JavaExtensions.DependencyExtension());
    registerExtension("jars", new JavaExtensions.JarsExtension());
    registerExtension("imports", new JavaExtensions.ImportsExtension());
    registerExtension("text", new JavaExtensions.TextExtension());
    registerExtension("json", new JavaExtensions.JsonExtension());

    // Register the standard dependency resolver by default
    registerResolver("file", new JavaResolvers.FileResolver());
    registerResolver("maven", new JavaResolvers.MavenResolver());

    // The state resulting from executing code
    _state = new JavaShellState();

    // Default the class loader to the system one initially.
    _classLoader = ClassLoader.getSystemClassLoader();

    // Add a reference to the default runtime jar
    String resourcePath = ClassLoader.getSystemResource("java/lang/String.class").getPath();
    String runtimePath = resourcePath.substring(resourcePath.indexOf(":") + 1,
                                                resourcePath.indexOf("!/"));
    _jars.add(runtimePath);

    // Import a few packages by default
    addImport("java.io.*", /* staticImport */ false);
    addImport("java.util.*", /* staticImport */ false);
    addImport("java.net.*", /* staticImport */ false);

  }

  /**
   * Gets the set of imports declared in the shell.
   * @return the list of imports.
   */
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
   * Gets the set of jars referenced in the shell.
   * @return the list of jars.
   */
  public String[] getJars() {
    String[] jars = new String[_jars.size()];
    jars = _jars.toArray(jars);

    for (int i = 0; i < jars.length; i++) {
      jars[i] = Paths.get(jars[i]).toFile().getName();
    }

    return jars;
  }

  /**
   * Gets the current set of variables declared in the shell.
   * @return a set of name/value pairs.
   */
  public JavaShellState getState() {
    return _state;
  }

  /**
   * Adds the specified dependency to the shell.
   * @param uri the URI that identifies the dependency.
   */
  public void addDependency(URI uri) throws IllegalArgumentException {
    String dependencyKey = uri.toString();
    if (_dependencies.containsKey(dependencyKey)) {
      return;
    }

    if (!uri.isAbsolute()) {
      throw new IllegalArgumentException("The URI used to identify a dependency must be absolute.");
    }

    DependencyResolver resolver = _resolvers.get(uri.getScheme());
    if (resolver == null) {
      throw new IllegalArgumentException("Unknown dependency type '" + uri.getScheme() + "'.");
    }

    Dependency dependency = resolver.resolve(uri);

    // Chain a class loader to enable loading types from the referenced dependency
    _classLoader = dependency.createClassLoader(_classLoader);

    // Add references to all the jars from the dependency so they can be used during compilation.
    for (String jar: dependency.getJars()) {
      _jars.add(jar);
    }

    _dependencies.put(dependencyKey, dependency);
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
    ExtensionDataParser parser = new ExtensionDataParser();
    ExtensionData extensionData = parser.parse(data);

    if (extensionData == null) {
      throw new EvaluationError("Invalid syntax.");
    }

    String name = extensionData.getName();
    Extension extension = _extensions.get(name);
    if (extension == null) {
      throw new EvaluationError("Invalid syntax. Unknown identifier '" + name + "'");
    }

    return extension.evaluate(this, extensionData.getDeclaration(), extensionData.getContent());
  }

  /**
   * Process the results of compiling a set of class members or a code block, i.e. restore old
   * shell state, execute new code, and then update shell state with resulting updates.
   * @param id the ID to use to generate unique names.
   * @param snippet the compiled snippet.
   * @return the result of a code block execution, or null for class members execution or if there
   *         is an error.
   */
  private Object processCode(int id, Snippet snippet) throws Exception {
    SnippetCompilation compilation = snippet.getCompilation();
    ClassLoader classLoader = new CodeClassLoader(_classLoader, id, compilation.getTypes());

    Class<?> snippetClass = classLoader.loadClass(snippet.getClassName());
    Object instance = snippetClass.newInstance();

    // Initialize the callable code instance with any current state
    boolean staleState = false;
    for (String variable: _state.getNames()) {
      Field field = snippetClass.getDeclaredField(variable);
      Object value = _state.getValue(variable);

      try {
        field.set(instance, value);
      }
      catch (IllegalArgumentException e) {
        _state.undeclareField(variable);
        staleState = true;

        String error = String.format(JavaShell.ERROR_TYPE_REDECLARED,
                                     variable,
                                     value.getClass().toString());
        System.err.println(error);
      }
    }

    if (staleState) {
      // Old state is stale, and the new instance was not fully initialized. So simply
      // bail out, rather than run with un-predictable results.
      return null;
    }

    // Execute the code
    Object result = ((Callable<?>)instance).call();

    if (snippet.getType() == SnippetType.CodeMembers) {
      // If the snippet represented a set of class members, then add any declared fields
      // to be tracked in state

      for (SnippetCodeMember member: snippet.getCodeMembers()) {
        if (member.isField()) {
          _state.declareField(member.getName(), member.getType());
        }
        else {
          _state.declareMethod(member.getName(), member.getCode());
        }
      }

      // The result of execution is the instance to be used to retrieve updated state.
      // This is because the rewriter puts new class members declared in a nested class
      // that is instantiated and returned when call() is invoked.
      instance = result;
    }

    // Now extract any new/updated state to be tracked for use in future evaluations.
    Class<?> instanceClass = instance.getClass();
    for (String name: _state.getNames()) {
      try {
        Field field = instanceClass.getDeclaredField(name);
        field.setAccessible(true);

        _state.setValue(name, field.get(instance));
      }
      catch (NoSuchFieldException e) {
        // Ignore. This particular field was not declared or updated in the case of
        // a set of class members.
      }
    }

    if (snippet.getType() == SnippetType.CodeMembers) {
      // For class members, the result is simply a shim class containing the newly defined
      // members, i.e. not meaningful to return out of the shell.
      return null;
    }

    return result;
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
  public void registerExtension(String name, Extension extension) {
    _extensions.put(name, extension);
  }

  /**
   * Registers a resolver that can be used to resolve dependency URIs.
   * @param name the name of the resolver, used to match against scheme in dependency URIs.
   * @param resolver the resolver instance to register.
   */
  public void registerResolver(String name, DependencyResolver resolver) {
    _resolvers.put(name, resolver);
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

    if (snippet.getType() == SnippetType.CompilationImports) {
      for (SnippetImport importReference: snippet.getImports()) {
        addImport(importReference.getName(), importReference.isStatic());
      }

      return null;
    }

    JavaRewriter rewriter = new JavaRewriter(this);
    snippet.setRewrittenCode(rewriter.rewrite(snippet));

    SnippetCompiler compiler = new SnippetCompiler(_jars, _packages, _types);
    SnippetCompilation compilation = compiler.compile(snippet);

    if (!compilation.hasErrors()) {
      snippet.setCompilation(compilation);

      Object result = null;
      if (snippet.getType() == SnippetType.CompilationUnit) {
        processCompilationUnit(evaluationID, snippet);
      }
      else {
        result = processCode(evaluationID, snippet);
      }

      return result;
    }
    else {
      // Raise an error for compilation errors
      StringBuilder errorBuilder = new StringBuilder();
      for (String error : compilation.getErrors()) {
        errorBuilder.append(error);
        errorBuilder.append("\n");
      }

      throw new EvaluationError(errorBuilder.toString());
    }
  }


  /**
   * A class loader that holds on to classes declared within the shell.
   */
  private final class ShellClassLoader extends JavaByteCodeLoader {

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
  private final class CodeClassLoader extends JavaByteCodeLoader {

    private final Map<String, byte[]> _types;

    /**
     * Initializes an instance of a CodeBlockClassLoader.
     * @param parentClassLoader the parent class loader to chain with.
     * @param id the ID of this class loader.
     * @param types the set of byte code buffers for types keyed by class names.
     */
    public CodeClassLoader(ClassLoader parentClassLoader, int id,
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
