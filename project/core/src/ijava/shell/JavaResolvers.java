// JavaResolvers.java
//

package ijava.shell;

import java.net.*;

/**
 * Standard Java-language related resolvers.
 */
public final class JavaResolvers {

  private JavaResolvers() {
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
