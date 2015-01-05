// JavaByteCodeLoader.java
//

package ijava.shell;

import java.util.*;

/**
 * Base class for class loaders that load classes from in-memory byte arrays.
 */
public abstract class JavaByteCodeLoader extends ClassLoader {

  private final long _id;
  private final HashMap<String, Class<?>> _loadedClasses;

  /**
   * Initializes an instance of a ByteCodeClassLoader.
   * @param parentClassLoader the parent class loader to chain with.
   * @param id the ID of this class loader.
   */
  protected JavaByteCodeLoader(ClassLoader parentClassLoader, long id) {
    super(parentClassLoader);
    _id = id;
    _loadedClasses = new HashMap<String, Class<?>>();
  }

  public long getId() {
    return _id;
  }

  protected abstract byte[] getByteCode(String name);

  /**
   * {@link ClassLoader}
   */
  @Override
  protected synchronized Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {
    // This method is overridden even though the docs suggest overriding findClass instead, so
    // that class lookup is satisfied by the most recent class loader, i.e. deepest in the tree
    // of class loaders, rather than use the default of starting at the top.

    Class<?> result = findLoadedClass(name);
    if (result != null) {
      return result;
    }

    if (_loadedClasses.containsKey(name)) {
      result = _loadedClasses.get(name);
    }
    else {
      byte[] bytes = getByteCode(name);
      if (bytes != null) {
        result = defineClass(name, bytes, 0, bytes.length);
        _loadedClasses.put(name, result);
      }
    }

    if (result != null) {
      if (resolve) {
        resolveClass(result);
      }
      return result;
    }
    else {
      return getParent().loadClass(name);
    }
  }
}
