// Data.java
//

package ijava.data;

import com.fasterxml.jackson.jr.ob.*;
import java.util.*;

/**
 * Represents a raw value that can be formatted in different ways.
 */
public final class Data {

  private final Object _value;
  private boolean _json;

  /**
   * Initializes an instance of Data with the specified value.
   * @param value the data value that this object represents.
   */
  public Data(Object value) {
    _value = value;
    _json = true;
  }

  /**
   * Formats the contained value as JSON.
   * @return the modified Data instance.
   */
  public Data json() {
    _json = true;
    return this;
  }

  /**
   * Generates a mime representation of this object.
   * @return a text/html representation of this object.
   */
  public Map<String, String> toMimeRepresentation() throws Exception {
    String serializedValue = null;

    if (_json) {
      serializedValue = JSON.std.asString(_value);
    }

    HashMap<String, String> representations = new HashMap<String, String>();
    representations.put("application/json", serializedValue);

    return representations;
  }
}
