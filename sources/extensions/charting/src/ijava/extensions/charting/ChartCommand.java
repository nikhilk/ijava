// ChartCommand.java
//

package ijava.extensions.charting;

import java.util.*;
import ijava.data.*;
import ijava.extensibility.*;

public final class ChartCommand implements Command {

  private static final String SCRIPT_TEMPLATE =
      "var config = {\n" +
      "  paths: {\n" +
      "    d3: '//cdnjs.cloudflare.com/ajax/libs/d3/3.5.3/d3.min',\n" +
      "    topojson: '//cdnjs.cloudflare.com/ajax/libs/topojson/1.6.9/topojson.min',\n" +
      "    vega: '//cdnjs.cloudflare.com/ajax/libs/vega/1.4.3/vega.min'\n" +
      "  }\n" +
      "};\n" +
      "requirejs.config(config);\n" +
      "require(['vega'], function(vega) {\n" +
      "  var chartSpec = %s;\n" +
      "  vega.parse.spec(chartSpec, function(chart) {\n" +
      "    chart({el:dom}).update();\n" +
      "  });\n" +
      "});";
  
	private final Shell _shell;

	public ChartCommand(Shell shell) {
		_shell = shell;
	}

	@Override
	public Object evaluate(String arguments, String data, int evaluationID,
												 Map<String, Object> metadata) throws Exception {
	  String script = String.format(SCRIPT_TEMPLATE, data);

    HTML html = new HTML("");
	  return html.addScript(script);
	}
}
