// StaticHTML.java
//

package ijava.data;

import java.util.*;

/**
 * Represents HTML markup display data.
 */
public final class StaticHTML implements Displayable {

  private final String _markup;

  /**
   * Initializes an instance of StaticHTML display data.
   * @param markup the HTML markup.
   */
  public StaticHTML(String markup) {
    _markup = markup;
  }

  /**
   * Gets the HTML markup.
   * @return the markup data.
   */
  public String getMarkup() {
    return _markup;
  }

  /**
   * {@link DisplayData}
   */
  @Override
  public Map<String, String> toDisplayData() {
    HashMap<String, String> representations = new HashMap<String, String>();
    representations.put("text/html", _markup);

    return representations;
  }
}
