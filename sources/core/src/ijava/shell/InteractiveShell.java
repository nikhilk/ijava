// InteractiveShell.java
//

package ijava.shell;

import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import ijava.*;
import ijava.extensibility.*;
import ijava.shell.compiler.*;

/**
 * Provides the interactive shell or REPL functionality for Java.
 */
public final class InteractiveShell implements Shell {

  public static final Log Log = ijava.Log.createLog("ijava.shell");

  private final static String ERROR_TYPE_REDECLARED =
      "The type of the variable '%s', '%s', has changed, and its value is no longer usable.\n" +
          "Please run the code to re-initialize that variable first, or simply re-run this code " +
          "to ignore the error and discard it.";

  private final HashMap<String, Command<?>> _commands;
  private final HashMap<String, Command<?>> _jsonCommands;
  private final HashMap<String, DependencyResolver> _resolvers;
  private final HashMap<String, Object> _extensions;

  private final HashMap<String, Dependency> _dependencies;
  private final HashSet<String> _jars;
  private final HashSet<String> _imports;
  private final HashSet<String> _staticImports;
  private final HashSet<String> _packages;
  private final HashMap<String, byte[]> _types;

  private final InteractiveState _state;

  private ClassLoader _classLoader;
  private String _cachedImports;

  /**
   * Initializes an instance of an InteractiveShell.
   */
  public InteractiveShell() {
    _commands = new HashMap<String, Command<?>>();
    _jsonCommands = new HashMap<String, Command<?>>();
    _resolvers = new HashMap<String, DependencyResolver>();
    _extensions = new HashMap<String, Object>();

    _dependencies = new HashMap<String, Dependency>();
    _jars = new HashSet<String>();
    _imports = new HashSet<String>();
    _staticImports = new HashSet<String>();
    _packages = new HashSet<String>();
    _types = new HashMap<String, byte[]>();
    _state = new InteractiveState();

    _classLoader = ClassLoader.getSystemClassLoader();
  }

  /**
   * Gets the current set of variables declared in the shell.
   * @return a set of name/value pairs.
   */
  public InteractiveState getState() {
    return _state;
  }

  /**
   * Adds a shell extension to the current shell.
   * @param name the name of the extension to lookup.
   * @return an optional object to indicate the extension was loaded.
   * @throws Exception if the specified extension could not be found or is invalid.
   */
  public Object addExtension(String name) throws Exception {
    if (_extensions.containsKey(name)) {
      return _extensions.get(name);
    }

    Class<?> extensionInterface = ShellExtension.class;
    Class<?> extensionClass = _classLoader.loadClass(name);

    if (!extensionInterface.isAssignableFrom(extensionClass)) {
      throw new IllegalArgumentException("The specified name '" + name +
          "' is not a valid extension.");
    }

    ShellExtension extension = (ShellExtension)extensionClass.newInstance();
    Object extensionResult = extension.initialize(this);

    _extensions.put(name, extensionResult);
    return extensionResult;
  }

  /**
   * Initializes an instance of an InteractiveShell.
   * @param appURL the URL of the application, to be used to resolve dependency paths.
   * @param dependencies the list of dependencies to pre-load, as well as include in compilation.
   * @param shellDependencies the list of shell-only dependencies to pre-load.
   * @param extensions the list of extensions to pre-load.
   * @throws Exception if there is an error during initialization.
   */
  public void initialize(URL appURL,
                         List<String> dependencies,
                         List<String> shellDependencies,
                         List<String> extensions) throws Exception {
    // Register the commands offered for shell functionality
    registerCommand("load", new InteractiveCommands.LoadCommand(this));
    registerCommand("values", new InteractiveCommands.ValuesCommand(this));

    // Register the commands offered for data creation/rendering functionality
    registerCommand("text", new DataCommands.TextCommand(this));
    registerCommand("json", new DataCommands.JsonCommand(this));
    registerCommand("html", new DataCommands.HTMLCommand(this));
    registerCommand("javascript", new DataCommands.JavaScriptCommand(this));

    // Register a few java language related commands by default
    registerCommand("dependency", new JavaCommands.DependencyCommand(this));
    registerCommand("jars", new JavaCommands.JarsCommand(this));
    registerCommand("imports", new JavaCommands.ImportsCommand(this));

    // Register the standard dependency resolver by default
    registerResolver("file", new JavaResolvers.FileResolver());
    registerResolver("maven", new JavaResolvers.MavenResolver());

    // Add a reference to the default java runtime jar as well as the ijava runtime jar.
    // These don't have to be loaded into a class loader, since they've already been loaded
    // by the time we get here.
    String resourcePath = ClassLoader.getSystemResource("java/lang/String.class").getPath();
    String javaRuntimePath = resourcePath.substring(resourcePath.indexOf(":") + 1,
                                                    resourcePath.indexOf("!/"));
    String ijavaRuntimePath = new URL(appURL, "ijavart.jar").getPath();

    _jars.add(javaRuntimePath);
    _jars.add(ijavaRuntimePath);

    // Import a few packages by default
    addImport("java.io.*", /* staticImport */ false);
    addImport("java.util.*", /* staticImport */ false);
    addImport("java.net.*", /* staticImport */ false);
    addImport("ijava.JavaHelpers.*", /* staticImport */ true);

    // Load up the dependencies - all of them get loaded via a class loader.
    // Only dependencies are tracked and made available as references during compilation.
    if (!dependencies.isEmpty() || !shellDependencies.isEmpty()) {
      URL[] dependencyJars = new URL[dependencies.size() + shellDependencies.size()];
      int i = 0;

      for (String dependency: dependencies) {
        dependencyJars[i] = new URL(appURL, dependency);
        _jars.add(dependencyJars[i].getPath());

        i++;
      }

      for (String dependency: shellDependencies) {
        dependencyJars[i] = new URL(appURL, dependency);
        i++;
      }

      _classLoader = new URLClassLoader(dependencyJars, _classLoader);
    }

    if (!extensions.isEmpty()) {
      for (String name: extensions) {
        addExtension(name);
      }
    }
  }

  /**
   * Invokes a command for the specified evaluation input.
   * @param data the evaluation text.
   * @param evaluationID the evaluation sequence number.
   */
  @SuppressWarnings("unchecked")
  private Object invokeCommand(String data, long evaluationID,
                               Map<String, Object> metadata) throws Exception {
    CommandData commandData = CommandData.parse(data);

    if (commandData == null) {
      throw new EvaluationError("Invalid syntax.");
    }

    String name = commandData.getName();
    Command<?> command = null;

    command = _commands.get(name);
    if (command == null) {
      int dataTypeLength = ShellData.JSON.length();

      // Check if this is a data command, which take the form:
      // <data type>.<command name>
      if (name.startsWith(ShellData.JSON) &&
          (name.length() > (dataTypeLength + 1)) &&
          (name.charAt(dataTypeLength) == '.')) {
        name = name.substring(dataTypeLength + 1);
        command = _jsonCommands.get(name);
      }
    }

    if (command == null) {
      throw new EvaluationError("Invalid syntax. Unknown command identifier '" + name + "'");
    }

    CommandOptions options = commandData.toOptions(command);
    if (options != null) {
      return ((Command<CommandOptions>)command).evaluate(options, evaluationID, metadata);
    }

    throw new EvaluationError("Invalid syntax. Unable to parse options for command '" + name + "'");
  }

  /**
   * Allows a derived shell to inspect or modify a compiled snippet.
   * @param snippet the successfully compiled snippet.
   */
  protected void onSnippetCompiled(Snippet snippet) {
  }

  /**
   * Allows a derived shell to inspect or modify the result of a snippet evaluation.
   * @param snippet the evaluated snippet.
   * @param result the result of successful evaluation.
   * @return the result to return from the shell.
   */
  protected Object onSnippetEvaluated(Snippet snippet, Object result) {
    return result;
  }

  /**
   * Allows a derived shell to inspect or modify a parsed snippet.
   * @param snippet the parsed snippet.
   */
  protected void onSnippetParsed(Snippet snippet) {
  }

  /**
   * Allows a derived shell to inspect or modify a rewritten snippet.
   * @param snippet the rewritten snippet.
   */
  protected void onSnippetRewritten(Snippet snippet) {
  }

  /**
   * Process the results of compiling a set of class members or a code block, i.e. restore old
   * shell state, execute new code, and then update shell state with resulting updates.
   * @param id the ID to use to generate unique names.
   * @param snippet the compiled snippet.
   * @return the result of a code block execution, or null for class members execution or if there
   *         is an error.
   */
  private Object processCode(long id, Snippet snippet) throws Exception {
    SnippetCompilation compilation = snippet.getCompilation();
    ClassLoader classLoader = new CodeClassLoader(_classLoader, id, compilation.getTypes());

    Class<?> snippetClass = classLoader.loadClass(snippet.getClassName());
    Object instance = snippetClass.newInstance();

    // Initialize the callable code instance with any current state
    boolean staleState = false;
    for (String variable: _state.getFields()) {
      Field field = snippetClass.getDeclaredField(variable);
      Object value = _state.getValue(variable);

      try {
        field.set(instance, value);
      }
      catch (IllegalArgumentException e) {
        _state.undeclareField(variable);
        staleState = true;

        String error = String.format(InteractiveShell.ERROR_TYPE_REDECLARED,
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
    for (String name: _state.getFields()) {
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
  private void processCompilationUnit(long id, Snippet snippet) {
    SnippetCompilation compilation = snippet.getCompilation();

    for (String packageName : compilation.getPackages()) {
      _packages.add(packageName);
      addImport(packageName + ".*", /* staticImport */ false);
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
   * {@link Evaluator}
   */
  @Override
  public Object evaluate(String data, long evaluationID,
                         Map<String, Object> metadata) throws Exception {
    if (evaluationID == 0) {
      evaluationID = (new Date()).getTime();
    }

    if (data.startsWith("%")) {
      return invokeCommand(data, evaluationID, metadata);
    }

    // Parse the data as code into a Snippet object
    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(data, evaluationID);
    }
    catch (SnippetException e) {
      throw new EvaluationError(e.getMessage(), e);
    }

    onSnippetParsed(snippet);

    // If the code was simply a set of imports, theres not much else to do besides
    // just tracking the import references. This allows using normal java syntax for imports.
    if (snippet.getType() == SnippetType.CompilationImports) {
      for (SnippetImport importReference: snippet.getImports()) {
        addImport(importReference.getName(), importReference.isStatic());
      }

      return null;
    }

    // Rewrite the snippet, so it is always a compilable unit of java code.
    JavaRewriter rewriter = new JavaRewriter(this);
    snippet.setRewrittenCode(rewriter.rewrite(snippet));

    onSnippetRewritten(snippet);

    // Compile the snippet into a set of classes.
    SnippetCompiler compiler = new SnippetCompiler(_jars, _packages, _types);
    SnippetCompilation compilation = compiler.compile(snippet);

    if (!compilation.hasErrors()) {
      snippet.setCompilation(compilation);

      onSnippetCompiled(snippet);

      // Evaluate the snippet, i.e. execute the code. If the snippet was defining types
      // then simply track the types declared in the shell instead.
      Object result = null;
      if (snippet.getType() == SnippetType.CompilationUnit) {
        processCompilationUnit(evaluationID, snippet);
      }
      else {
        result = processCode(evaluationID, snippet);
        result = onSnippetEvaluated(snippet, result);
      }

      snippet.generateMetadata(metadata);
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
   * {@link Shell}
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
   * {@link Shell}
   */
  @Override
  public String[] getReferences() {
    String[] jars = new String[_jars.size()];
    jars = _jars.toArray(jars);

    for (int i = 0; i < jars.length; i++) {
      jars[i] = Paths.get(jars[i]).toFile().getName();
    }

    return jars;
  }

  /**
   * {@link Shell}
   */
  @Override
  public Class<?> getType(String name) {
    try {
      return _classLoader.loadClass(name);
    }
    catch (ClassNotFoundException e) {
      return null;
    }
  }

  /**
   * {@link Shell}
   */
  @Override
  public Set<String> getTypeNames() {
    return _types.keySet();
  }

  /**
   * {@link Shell}
   */
  @Override
  public Object getVariable(String name) {
    return _state.getValue(name);
  }

  /**
   * {@link Shell}
   */
  @Override
  public Set<String> getVariableNames() {
    return _state.getFields();
  }

  /**
   * {@link Shell}
   */
  @Override
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

    Dependency dependency = new Dependency(uri, resolver.resolve(uri));
    _dependencies.put(dependencyKey, dependency);

    // Add references to all the jars from the dependency so they can be used during compilation.
    for (String jar: dependency.getJars()) {
      _jars.add(jar);
    }

    // Chain a class loader to enable loading types from the referenced dependency
    _classLoader = dependency.createClassLoader(_classLoader);
  }

  /**
   * Adds a package to be imported for subsequent compilations.
   * @param importName the package or type to be imported.
   * @param staticImport whether the import should be a static import of a type.
   */
  @Override
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
   * {@link Shell}
   */
  @Override
  public void declareVariable(String name, String type) {
    _state.declareField(name, type);
  }

  /**
   * {@link Shell}
   */
  @Override
  public void registerCommand(String name, Command<?> command) {
    _commands.put(name, command);
  }

  /**
   * {@link Shell}
   */
  @Override
  public void registerDataCommand(String name, String dataType, Command<?> command) {
    if (dataType.equals(ShellData.JSON)) {
      _jsonCommands.put(name, command);
    }
  }

  /**
   * {@link Shell}
   */
  @Override
  public void registerResolver(String name, DependencyResolver resolver) {
    _resolvers.put(name, resolver);
  }

  /**
   * {@link Shell}
   */
  @Override
  public void resetVariable(String name) throws IllegalArgumentException {
    _state.resetValue(name);
  }

  /**
   * {@link Shell}
   */
  @Override
  public void setVariable(String name, Object value) throws IllegalArgumentException {
    _state.setValue(name, value);
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
    public ShellClassLoader(ClassLoader parentClassLoader, long id, HashSet<String> names) {
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
    public CodeClassLoader(ClassLoader parentClassLoader, long id,
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
