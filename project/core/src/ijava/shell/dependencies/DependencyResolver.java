// DependencyResolver.java
//

package ijava.shell.dependencies;

import java.net.*;

/**
 * Provides the functionality to resolve URIs into dependency objects.
 */
public interface DependencyResolver {

  /**
   * Resolves the specified URI into a dependency object.
   * @param uri the identifier of the dependency.
   * @return the resolved dependency object.
   */
  public Dependency resolve(URI uri) throws IllegalArgumentException;
}
