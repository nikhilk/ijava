// ByteCodeClassLoader.java
//

package ijava.shell.util;

import java.util.*;

/**
 * Base class for class loaders that load classes from in-memory byte arrays.
 */
public abstract class ByteCodeClassLoader extends ClassLoader {

  private final HashMap<String, Class<?>> _loadedClasses;

  /**
   * Initializes an instance of a ByteCodeClassLoader.
   * @param parentClassLoader the parent class loader to chain with.
   */
  public ByteCodeClassLoader(ClassLoader parentClassLoader) {
    super(parentClassLoader);
    _loadedClasses = new HashMap<String, Class<?>>();
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (_loadedClasses.containsKey(name)) {
      return _loadedClasses.get(name);
    }

    byte[] bytes = getByteCode(name);
    if (bytes != null) {
      Class<?> newClass = defineClass(name, bytes, 0, bytes.length);
      resolveClass(newClass);

      _loadedClasses.put(name, newClass);
      return newClass;
    }

    return super.findClass(name);
  }

  protected abstract byte[] getByteCode(String name);
}
