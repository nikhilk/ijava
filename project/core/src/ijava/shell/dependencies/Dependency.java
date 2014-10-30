// Dependency.java
//

package ijava.shell.dependencies;

import java.net.*;

/**
 * Represents a dependency referenced within the shell.
 */
public final class Dependency {

  private final URI _uri;
  private final ClassLoader _classLoader;

  /**
   * Initializes an instance of a Dependency.
   * @param uri the URI used to identify the dependency.
   * @param classLoader the ClassLoader instance used to load classes from the dependency.
   */
  public Dependency(URI uri, ClassLoader classLoader) {
    _uri = uri;
    _classLoader = classLoader;
  }

  /**
   * Gets the ClassLoader to load classes from the dependency.
   * @return the ClassLoader representing the dependency.
   */
  public ClassLoader getClassLoader() {
    return _classLoader;
  }

  /**
   * Gets the URI that identifies the dependency.
   * @return the dependency identifier.
   */
  public URI getURI() {
    return _uri;
  }
}
