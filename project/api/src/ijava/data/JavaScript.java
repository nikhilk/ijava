// JavaScript.java
//

package ijava.data;

import java.util.*;

/**
 * Represents javascript to execute on the client.
 */
public final class JavaScript {

  private final String _script;
  private final List<Map.Entry<String, String>> _dependencies;

  private String _alternateText;

  /**
   * Initializes an instance of JavaScript.
   * @param script the client-side script.
   */
  public JavaScript(String script) {
    _script = script;
    _dependencies = new ArrayList<Map.Entry<String, String>>();
  }

  public JavaScript addAlternateText(String text) {
    _alternateText = text;
    return this;
  }

  public JavaScript addDependency(String module, String variable) {
    _dependencies.add(new AbstractMap.SimpleEntry<String, String>(module, variable));
    return this;
  }

  private String generateScript() {
    StringBuilder sb = new StringBuilder();

    sb.append("require([");

    boolean firstModule = true;
    for (Map.Entry<String, String> dependency: _dependencies) {
      if (!firstModule) {
        sb.append(", ");
      }

      sb.append("'");
      sb.append(dependency.getKey());
      sb.append("'");

      firstModule = false;
    }

    sb.append("], function(");

    boolean firstVariable = true;
    for (Map.Entry<String, String> dependency: _dependencies) {
      if (!firstVariable) {
        sb.append(", ");
      }

      sb.append(dependency.getValue());
      firstVariable = false;
    }

    sb.append(") {\n");
    sb.append(_script);
    sb.append("\n});");

    return sb.toString();
  }

  /**
   * Generates a mime representation of this object.
   * @return the display representation of this object.
   */
  public Map<String, String> toMimeRepresentation() {
    HashMap<String, String> representations = new HashMap<String, String>();
    if ((_alternateText != null) && !_alternateText.isEmpty()) {
      representations.put("text/plain", _alternateText);
    }

    representations.put("application/javascript", generateScript());

    return representations;
  }
}
