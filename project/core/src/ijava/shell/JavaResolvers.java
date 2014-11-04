// JavaResolvers.java
//

package ijava.shell;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

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
      String[] pathParts = uri.getPath().split("/");
      if (pathParts.length != 4) {
        throw new IllegalArgumentException("Invalid maven artifact reference. " +
            "The URL must be of the form maven:///group/artifact/version");
      }

      String groupId = pathParts[1];
      String artifactId = pathParts[2];
      String version = pathParts[3];

      MavenRepository repository = new MavenRepository();
      List<String> jars = repository.resolveArtifact(groupId, artifactId, version);

      if ((jars == null) || (jars.size() == 0)) {
        throw new IllegalArgumentException("Could not resolve the specified maven artifact.");
      }

      return new Dependency(uri, jars);
    }
  }
}
