// Shell.java
//

package ijava.extensibility;

import java.net.*;
import java.util.*;

/**
 * Shell functionality exposed to shell plugins.
 */
public interface Shell extends Evaluator {

  /**
   * Gets the set of imports declared in the shell.
   * @return the list of imports.
   */
  public String getImports();

  /**
   * Gets the set of jars referenced in the shell.
   * @return the list of jars.
   */
  public String[] getReferences();

  /**
   * Gets the specified type defined or referenced within the shell.
   * @param name the name of the type to lookup.
   * @return the resulting class, or null if not found.
   */
  public Class<?> getType(String name);

  /**
   * Gets the set of type names defined by the user within the shell.
   * @return the set of declared type names.
   */
  public Set<String> getTypeNames();

  /**
   * Gets the value of a variable.
   * @param name the name of the variable to lookup.
   * @return the value of the variable or null if not found.
   */
  public Object getVariable(String name);

  /**
   * Gets the set of names of all declared variables.
   * @return the set of names.
   */
  public Set<String> getVariableNames();

  /**
   * Adds the specified dependency to the shell.
   * @param uri the URI that identifies the dependency.
   */
  public void addDependency(URI uri);

  /**
   * Adds a package to be imported for subsequent compilations.
   * @param importName the package or type to be imported.
   * @param staticImport whether the import should be a static import of a type.
   */
  public void addImport(String importName, boolean staticImport);

  /**
   * Adds a new variable, or re-declares an existing variable with a new type.
   * @param name the name of the variable to declare.
   * @param type the type associated with the variable.
   */
  public void declareVariable(String name, String type);

  /**
   * Registers a command so it may be invoked within the shell.
   * @param name the name of the command used in invoking it.
   * @param command the Command implementation to be registered.
   */
  public void registerCommand(String name, Command<?> command);

  /**
   * Registers a command so it may be invoked within the shell.
   * @param name the name of the command used in invoking it.
   * @param dataType the type of data to register for.
   * @param command the Command implementation to be registered.
   */
  public void registerDataCommand(String name, String dataType, Command<?> command);

  /**
   * Registers a resolver that can be used to resolve dependency URIs.
   * @param name the name of the resolver, used to match against scheme in dependency URIs.
   * @param resolver the resolver instance to register.
   */
  public void registerResolver(String name, DependencyResolver resolver);

  /**
   * Resets the value of a variable. The variable must have been previously added.
   * @param name the name of the variable to reset.
   * @throws IllegalArgumentException if the variable was not found.
   */
  public void resetVariable(String name) throws IllegalArgumentException;

  /**
   * Sets the value of a variable. The variable must have been previously declared.
   * @param name the name of the variable to set.
   * @param value the new value of the variable.
   * @throws IllegalArgumentException if the variable was not found.
   */
  public void setVariable(String name, Object value) throws IllegalArgumentException;
}
