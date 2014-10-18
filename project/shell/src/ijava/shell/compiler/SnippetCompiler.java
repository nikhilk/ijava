// SnippetCompiler.java
//

package ijava.shell.compiler;

import java.util.*;
import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.batch.*;
import org.eclipse.jdt.internal.compiler.classfmt.*;
import org.eclipse.jdt.internal.compiler.env.*;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.problem.*;

/**
 * Compiles snippets into executable classes.
 */
public final class SnippetCompiler implements ICompilerRequestor, INameEnvironment {

  private final static CompilerOptions Options;
  private final static String RuntimePath;

  private final SnippetShell _shell;

  private final Compiler _compiler;
  private final INameEnvironment _references;

  private final Map<String, byte[]> _types;
  private final HashSet<String> _packages;

  static {
    HashMap<String, String> optionSet = new HashMap<String, String>();
    optionSet.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
    optionSet.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_7);
    optionSet.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_7);

    Options = new CompilerOptions(optionSet);

    String resourcePath = ClassLoader.getSystemResource("java/lang/String.class").getPath();
    RuntimePath = resourcePath.substring(resourcePath.indexOf(":") + 1,
                                         resourcePath.indexOf("!/"));
  }

  /**
   * Initializes an instance of a SnippetCompiler with the shell that is performing the
   * compilation.
   * @param shell the shell performing the compilation.
   */
  public SnippetCompiler(SnippetShell shell) {
    _shell = shell;

    String[] paths = new String[] { SnippetCompiler.RuntimePath };
    _references = new FileSystem(paths, null, "UTF-8");

    INameEnvironment nameEnvironment = this;
    ICompilerRequestor compilerRequestor = this;
    IErrorHandlingPolicy errorHandling = DefaultErrorHandlingPolicies.exitAfterAllProblems();
    IProblemFactory problemFactory = new DefaultProblemFactory();
    CompilerOptions options = SnippetCompiler.Options;

    _compiler = new Compiler(nameEnvironment, errorHandling, options, compilerRequestor,
                             problemFactory);

    _types = new HashMap<String, byte[]>();
    _packages = new HashSet<String>();
  }

  /**
   * Compiles the specified snippet.
   * @param snippet the snippet to compile.
   * @return whether the compilation succeeded.
   */
  public boolean compile(Snippet snippet) {
    ICompilationUnit[] units = new ICompilationUnit[] {
      new CompilationUnit(snippet.getRewrittenCode().toCharArray(),
                          snippet.getClassName() + ".java",
                          null)
    };

    _compiler.compile(units);

    // TODO: Handle errors

    SnippetCompilation compilation = new SnippetCompilation(_packages, _types);
    snippet.setCompilation(compilation);

    return true;
  }

  private NameEnvironmentAnswer lookupType(String name) {
    byte[] bytes = _shell.getTypes().get(name);
    if (bytes != null) {
      try {
        ClassFileReader classReader = new ClassFileReader(bytes, null);
        return new NameEnvironmentAnswer(classReader, null);
      }
      catch (ClassFormatException e) {
      }
    }

    return null;
  }

  /**
   * {@link ICompilerRequestor}
   */
  @Override
  public void acceptResult(CompilationResult result) {
    for (ClassFile classFile : result.getClassFiles()) {
      String name = new String(CharOperation.concatWith(classFile.getCompoundName(), '.'));
      _types.put(name, classFile.getBytes());

      int packageSeparatorIndex = name.lastIndexOf('.');
      if (packageSeparatorIndex > 0) {
        String packageName = name.substring(0, packageSeparatorIndex);
        _packages.add(packageName);
      }
    }
  }

  /**
   * {@link INameEnvironment}
   */
  @Override
  public void cleanup() {
    // Nothing to do here
  }

  /**
   * {@link INameEnvironment}
   */
  @Override
  public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
    String name = new String(CharOperation.concatWith(compoundTypeName, '.'));
    NameEnvironmentAnswer answer = lookupType(name);

    if (answer == null) {
      answer = _references.findType(compoundTypeName);
    }

    return answer;
  }

  /**
   * {@link INameEnvironment}
   */
  @Override
  public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
    String name = new String(CharOperation.concatWith(packageName, typeName, '.'));
    NameEnvironmentAnswer answer = lookupType(name);

    if (answer == null) {
      answer = _references.findType(typeName, packageName);
    }

    return answer;
  }

  /**
   * {@link INameEnvironment}
   */
  @Override
  public boolean isPackage(char[][] parentPackageName, char[] packgeName) {
    String name = new String(CharOperation.concatWith(parentPackageName, packgeName, '.'));
    if (_shell.getPackages().contains(name)) {
      return true;
    }

    NameEnvironmentAnswer answer = lookupType(name);
    if (answer != null) {
      return false;
    }

    return _references.isPackage(parentPackageName, packgeName);
  }
}

