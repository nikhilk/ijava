// SnippetCompiler.java
//

package ijava.shell.compiler;

import java.util.*;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.batch.*;
import org.eclipse.jdt.internal.compiler.env.*;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.problem.*;
import org.eclipse.jdt.internal.compiler.util.*;

/**
 * Compiles snippets into executable classes.
 */
public final class SnippetCompiler {

  private final static CompilerOptions Options;

  static {
    HashMap<String, String> optionSet = new HashMap<String, String>();

    optionSet.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
    optionSet.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_7);
    optionSet.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_7);

    Options = new CompilerOptions(optionSet);
  }

  public SnippetCompiler() {
  }

  /**
   * Compiles the specified snippet.
   * @param snippet the snippet to compile.
   * @return the compilation result from compiling the snippet.
   */
  public SnippetCompilation compile(Snippet snippet) {
    ArrayList<FileSystem.Classpath> classPaths = new ArrayList<FileSystem.Classpath>();
    Util.collectRunningVMBootclasspath(classPaths);

    String[] paths = new String[classPaths.size()];
    int i = 0;
    for (FileSystem.Classpath cp : classPaths) {
      paths[i] = cp.getPath();
      i++;
    }

    INameEnvironment nameEnvironment = new FileSystem(paths, null, "UTF-8");
    IErrorHandlingPolicy errorHandlingPolicy = DefaultErrorHandlingPolicies.exitAfterAllProblems();
    IProblemFactory problemFactory = new DefaultProblemFactory();

    CompilerTask compilerTask = new CompilerTask();
    CompilerOptions compilerOptions = SnippetCompiler.Options;

    ICompilationUnit[] units = new ICompilationUnit[] {
      new CompilationUnit(snippet.getRewrittenCode().toCharArray(),
                          snippet.getClassName() + ".java",
                          null)
    };

    org.eclipse.jdt.internal.compiler.Compiler compiler =
        new org.eclipse.jdt.internal.compiler.Compiler(nameEnvironment,
                                                       errorHandlingPolicy,
                                                       compilerOptions,
                                                       compilerTask,
                                                       problemFactory);
    compiler.compile(units);

    return new SnippetCompilation(compilerTask.getClassLoader());
  }


  private final class InMemoryClassLoader extends ClassLoader {

    private final Map<String, byte[]> _byteCode;

    public InMemoryClassLoader(Map<String, byte[]> byteCode) {
      super();
      _byteCode = byteCode;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      byte[] bytes = _byteCode.get(name);
      if (bytes == null) {
        return super.findClass(name);
      }
      else {
        return defineClass(name, bytes, 0, bytes.length);
      }
    }
  }


  private final class CompilerTask implements ICompilerRequestor {

    private ClassLoader _classLoader;

    public ClassLoader getClassLoader() {
      return _classLoader;
    }

    /**
     * {@link ICompilerRequestor}
     */
    @Override
    public void acceptResult(CompilationResult result) {
      HashMap<String, byte[]> byteCode = new HashMap<String, byte[]>();
      for (ClassFile classFile : result.getClassFiles()) {
        String name = new String(CharOperation.concatWith(classFile.getCompoundName(), '.'));
        byteCode.put(name, classFile.getBytes());
      }

      _classLoader = new InMemoryClassLoader(byteCode);
    }
  }
}
