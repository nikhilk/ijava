// ChartCommand.java
//

package ijava.extensions.charting;

import java.util.*;
import ijava.data.*;
import ijava.extensibility.*;

public final class ChartCommand implements Command {

  @Override
  public Object evaluate(String arguments, String data, long evaluationID,
                         Map<String, Object> metadata) throws Exception {
    String script = "charts.render(dom, " + data + ");";

    return new HTML("").addScript(script)
        .addScriptDependency("extensions/charting", "charts");
  }
}
