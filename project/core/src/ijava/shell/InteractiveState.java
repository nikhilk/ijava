// InteractiveState.java
//

package ijava.shell;

import java.util.*;

/**
 * Represents the state of the shell, i.e. all the methods, fields and their values.
 */
public final class InteractiveState {

  private final Map<String, String> _methods;
  private final Map<String, Set<String>> _fields;
  private final Map<String, Object> _values;

  private String _code;

  /**
   * Initializes an instance of InteractiveState.
   */
  public InteractiveState() {
    _methods = new HashMap<String, String>();
    _fields = new HashMap<String, Set<String>>();

    _values = new HashMap<String, Object>();
  }

  /**
   * Gets the set of field and method declarations.
   * @return code representing fields and methods.
   */
  public String getCode() {
    if (_code == null) {
      StringBuilder sb = new StringBuilder();

      for (Map.Entry<String, Set<String>> entry: _fields.entrySet()) {
        sb.append("public ");
        sb.append(entry.getKey());
        sb.append(" ");

        boolean firstName = true;
        for (String name: entry.getValue()) {
          if (!firstName) {
            sb.append(",");
          }

          sb.append(name);
          firstName = false;
        }

        sb.append(";\n");
      }

      for (Map.Entry<String, String> entry: _methods.entrySet()) {
        sb.append("\n");
        sb.append(entry.getValue());
        sb.append("\n");
      }

      _code = sb.toString();
    }

    return _code;
  }

  /**
   * Gets the set of names of all declared fields.
   * @return the set of names.
   */
  public Set<String> getNames() {
    return _values.keySet();
  }

  /**
   * Gets the value of a field.
   * @param name the name of the field to lookup.
   * @return the value of the field or null if not found.
   */
  public Object getValue(String name) {
    return _values.get(name);
  }

  /**
   * Sets the value of a field. The field must have been previously added.
   * @param name the name of the field to set.
   * @param value the new value of the field.
   * @throws IllegalArgumentException if the field was not found.
   */
  public void setValue(String name, Object value) throws IllegalArgumentException {
    if (!_values.containsKey(name)) {
      throw new IllegalArgumentException("Unknown field");
    }

    _values.put(name, value);
  }

  /**
   * Resets the value of a field. The field must have been previously added.
   * @param name the name of the field to reset.
   * @throws IllegalArgumentException if the field was not found.
   */
  public void resetValue(String name) throws IllegalArgumentException {
    if (!_values.containsKey(name)) {
      throw new IllegalArgumentException("Unknown field");
    }

    _values.put(name, null);
  }

  /**
   * Adds a new field, or re-declares an existing field with a new type.
   * @param name the name of the field to declare.
   * @param type the type associated with the field.
   */
  public void declareField(String name, String type) {
    boolean newField = false;

    if (_values.containsKey(name)) {
      Set<String> names = _fields.get(type);
      if ((names == null) || !names.contains(name)) {
        // The name is being re-declared with a different type, so remove the name and the
        // current value.
        undeclareField(name);

        // Since the name is being redeclared, throw away the old value
        _values.put(name, null);
        newField = true;
      }
    }
    else {
      _values.put(name, null);
      newField = true;
    }

    if (newField) {
      // Add the new name to the set of names associated in fields of a type
      Set<String> names = _fields.get(type);
      if (names == null) {
        names = new HashSet<String>();
        _fields.put(type, names);
      }

      names.add(name);
      _code = null;
    }
  }

  /**
   * Declares a method along with its implementation.
   * @param name the name of the method to add or update.
   * @param code the code representing the method.
   */
  public void declareMethod(String name, String code) {
    _methods.put(name, code);
    _code = null;
  }

  /**
   * Removes the specified field from state, along with its value.
   * @param name the field to remove.
   */
  public void undeclareField(String name) {
    if (_values.containsKey(name)) {
      _values.remove(name);

      for (Map.Entry<String, Set<String>> entry: _fields.entrySet()) {
        Set<String> names = entry.getValue();
        if (names.contains(name)) {
          names.remove(name);

          if (names.size() == 0) {
            // If there are no more names associated with this field type,
            // remove the entry altogether.
            _fields.remove(entry.getKey());
          }

          _code = null;
          break;
        }
      }
    }
  }

  /**
   * Removes the specified method from state.
   * @param name the method to remove.
   */
  public void undeclareMethod(String name) {
    if (_methods.containsKey(name)) {
      _methods.remove(name);
      _code = null;
    }
  }
}
