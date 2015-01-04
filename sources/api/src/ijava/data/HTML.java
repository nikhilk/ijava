// StaticHTML.java
//

package ijava.data;

import java.util.*;

/**
 * Represents HTML markup display data.
 */
public final class HTML {

  private final String _markup;
  private JavaScript _script;

  /**
   * Initializes an instance of HTML display data.
   * @param markup the HTML markup.
   */
  public HTML(String markup) {
    _markup = markup;
  }

  /**
   * Adds script to the HTML.
   * @param script the JavaScript source code.
   * @return the modified HTML object.
   */
  public HTML addScript(String script) {
    _script = new JavaScript(script);
    return this;
  }

  /**
   * Adds a dependency of the script.
   * @param module the name of the script dependency.
   * @param variable the name of the resolved script dependency.
   * @return the modified HTML object.
   */
  public HTML addScriptDependency(String module, String variable) {
    if (_script == null) {
      throw new IllegalStateException("Cannot add script dependencies without adding script first");
    }

    _script.addDependency(module, variable);
    return this;
  }

  /**
   * Generates a mime representation of this object.
   * @return a text/html representation of this object.
   */
  public Map<String, String> toMimeRepresentation() {
    String html = _markup;

    if (_script != null) {
      String id = "_" + (new Date()).getTime();

      StringBuilder sb = new StringBuilder();
      sb.append("<div id='" + id + "'>");
      sb.append(html);
      sb.append("</div>");
      sb.append("<script>\n");
      sb.append("(function(dom) {\n");
      sb.append(_script.generateScript());
      sb.append("})(document.getElementById('" + id + "'))\n");
      sb.append("</script>");

      html = sb.toString();
    }

    HashMap<String, String> representations = new HashMap<String, String>();
    representations.put("text/html", html);

    return representations;
  }
}
