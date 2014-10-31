// JavaResolvers.java
//

package ijava.shell;

import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * Standard Java-language related resolvers.
 */
public final class JavaResolvers {

  private JavaResolvers() {
  }

  /**
   * Resolves dependencies repesenting jar files.
   */
  public static final class FileResolver implements DependencyResolver {

    /**
     * {@link DependencyResolver}
     */
    @Override
    public Dependency resolve(URI uri) throws IllegalArgumentException {
      Path path = null;

      try {
        path = Paths.get(uri);
      }
      catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid file reference. " +
            "The URL must be of the form file://<full path>.");
      }

      File file = path.toFile();
      if (!file.exists() || file.isDirectory()) {
        throw new IllegalArgumentException("Invalid file reference. " +
            "The URL must refer to a .jar file.");
      }

      return new Dependency(uri, file.getPath());
    }
  }

  /**
   * Resolves dependencies representing Maven artifacts.
   */
  public static final class MavenResolver implements DependencyResolver {

    /**
     * {@link DependencyResolver}
     */
    @Override
    public Dependency resolve(URI uri) throws IllegalArgumentException {
      // TODO: Implement this
      return null;
    }
  }
}
