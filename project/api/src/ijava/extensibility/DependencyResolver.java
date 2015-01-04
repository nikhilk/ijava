// DependencyResolver.java
//

package ijava.extensibility;

import java.net.*;

/**
 * Provides the functionality to resolve URIs into dependency objects.
 */
public interface DependencyResolver {

  /**
   * Resolves the specified URI into a set of jars to be loaded.
   * @param uri the identifier of the dependency.
   * @return the list of jar paths resulting from the resolution process.
   */
  public String[] resolve(URI uri) throws IllegalArgumentException;
}
