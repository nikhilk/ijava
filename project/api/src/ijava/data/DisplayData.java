// DisplayData.java
//

package ijava.data;

import java.util.*;

/**
 * Represents a collection of mime/formatted data pairs that can be used to display an object.
 */
public final class DisplayData extends AbstractMap<String, String> {

  private final Map<String, String> _mimeMap;

  private DisplayData(Map<String, String> mimeMap) {
    _mimeMap = mimeMap;
  }

  @Override
  public Set<java.util.Map.Entry<String, String>> entrySet() {
    return _mimeMap.entrySet();
  }

  /**
   * Creates an instance of a DisplayData from an instance value.
   * @param value the value to be formatted.
   * @return a map of different mime representations for the specified value.
   */
  public static DisplayData create(Object value) {
    if (value == null) {
      return null;
    }

    // TODO: Interesting standard Java types to special case?

    Map<String, String> mimeMap;

    if (value instanceof Displayable) {
      mimeMap = ((Displayable)value).toDisplayData();
    }
    else {
      // Default to a textual representation produced via toString.
      mimeMap = new HashMap<String, String>();
      mimeMap.put("text/plain", value.toString());
    }

    return new DisplayData(mimeMap);
  }
}
