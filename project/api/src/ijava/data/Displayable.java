// Displayable.java
//

package ijava.data;

import java.util.*;

/**
 * Represents the contract of a data value that can be displayed on the client.
 */
public interface Displayable {

  /**
   * Gets the display representations of the data.
   * @return the mime and associated formatted representations of the data.
   */
  public Map<String, String> toDisplayData();
}
