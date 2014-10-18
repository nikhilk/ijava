// SnippetCompiler.java
//

package ijava.shell.compiler;

import java.util.*;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.batch.*;
import org.eclipse.jdt.internal.compiler.classfmt.*;
import org.eclipse.jdt.internal.compiler.env.*;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.problem.*;

/**
 * Compiles snippets into executable classes.
 */
public final class SnippetCompiler {

  private final static CompilerOptions Options;
  private final static String RuntimePath;

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

  public SnippetCompiler() {
  }

  /**
   * Compiles the specified snippet.
   * @param snippet the snippet to compile.
   * @param packages the set of previously created package names.
   * @param byteCode the byte buffers representing the set of previously defined classes.
   * @return the compilation result from compiling the snippet.
   */
  public SnippetCompilation compile(Snippet snippet,
                                    Set<String> packages, Map<String, byte[]> byteCode) {
    String[] paths = new String[] { SnippetCompiler.RuntimePath };

    INameEnvironment nameEnvironment = new FileSystem(paths, null, "UTF-8");
    IErrorHandlingPolicy errorHandlingPolicy = DefaultErrorHandlingPolicies.exitAfterAllProblems();
    IProblemFactory problemFactory = new DefaultProblemFactory();

    CompilerTask compilerTask = new CompilerTask(nameEnvironment, packages, byteCode);
    CompilerOptions compilerOptions = SnippetCompiler.Options;

    ICompilationUnit[] units = new ICompilationUnit[] {
      new CompilationUnit(snippet.getRewrittenCode().toCharArray(),
                          snippet.getClassName() + ".java",
                          null)
    };

    org.eclipse.jdt.internal.compiler.Compiler compiler =
        new org.eclipse.jdt.internal.compiler.Compiler(compilerTask,
                                                       errorHandlingPolicy,
                                                       compilerOptions,
                                                       compilerTask,
                                                       problemFactory);
    compiler.compile(units);

    return new SnippetCompilation(compilerTask.getPackages(), compilerTask.getByteCode());
  }


  /**
   * Represents the current compilation task. This processes the results of a compilation, as well
   * as resolves references to packages and classes.
   */
  private final class CompilerTask implements ICompilerRequestor, INameEnvironment {

    private final INameEnvironment _references;
    private final Set<String> _packages;
    private final Map<String, byte[]> _byteCode;

    private final Map<String, byte[]> _newByteCode;
    private final HashSet<String> _newPackages;

    public CompilerTask(INameEnvironment references,
                        Set<String> packages, Map<String, byte[]> byteCode) {
      _references = references;
      _packages = packages;
      _byteCode = byteCode;

      _newByteCode = new HashMap<String, byte[]>();
      _newPackages = new HashSet<String>();
    }

    public Map<String, byte[]> getByteCode() {
      return _newByteCode;
    }

    public Set<String> getPackages() {
      return _newPackages;
    }

    private NameEnvironmentAnswer lookupType(String name) {
      byte[] bytes = _byteCode.get(name);
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
        _newByteCode.put(name, classFile.getBytes());

        int packageSeparatorIndex = name.lastIndexOf('.');
        if (packageSeparatorIndex > 0) {
          String packageName = name.substring(0, packageSeparatorIndex);
          _newPackages.add(packageName);
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
      if (_packages.contains(name)) {
        return true;
      }

      NameEnvironmentAnswer answer = lookupType(name);
      if (answer != null) {
        return false;
      }

      return _references.isPackage(parentPackageName, packgeName);
    }
  }
}
