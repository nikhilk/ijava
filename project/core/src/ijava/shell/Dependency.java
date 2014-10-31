// Dependency.java
//

package ijava.shell;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Represents a dependency referenced within the shell.
 */
public final class Dependency {

  private final URI _uri;
  private final List<String> _jars;

  /**
   * Initializes an instance of a Dependency.
   * @param uri the URI used to identify the dependency.
   * @param jar the local file path of the associated jar.
   */
  public Dependency(URI uri, String jar) {
    this(uri, Arrays.asList(jar));
  }

  /**
   * Initializes an instance of a Dependency.
   * @param uri the URI used to identify the dependency.
   * @param jars the local file path of the associated jars.
   */
  public Dependency(URI uri, List<String> jars) {
    _uri = uri;
    _jars = jars;
  }

  /**
   * Gets the list of local file paths of associated jars.
   * @return the list of jars.
   */
  public List<String> getJars() {
    return _jars;
  }

  /**
   * Gets the URI that identifies the dependency.
   * @return the dependency identifier.
   */
  public URI getURI() {
    return _uri;
  }

  /**
   * Creates a class loader to load classes from the associated jars.
   * @param parent the parent class loader to parent the new one to.
   * @return the new chained class loader.
   */
  public ClassLoader createClassLoader(ClassLoader parent) {
    URL[] urls = new URL[_jars.size()];
    for (int i = 0; i < urls.length; i++) {
      File file = new File(_jars.get(i));
      try {
        urls[i] = file.toURI().toURL();
      }
      catch (MalformedURLException e) {
      }
    }

    return new URLClassLoader(urls, parent);
  }
}
