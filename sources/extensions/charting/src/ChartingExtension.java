// ChartingExtension.java
//

import ijava.extensibility.*;
import ijava.extensions.charting.*;

public final class ChartingExtension implements ShellExtension {

  /**
   * {@link ShellExtension}
   */
  @Override
  public Object initialize(Shell shell) {
    shell.registerDataCommand("chart", ShellData.JSON, new ChartCommand(shell));
    shell.registerCommand("_chartTable", new ChartTableCommand(shell));
    return null;
  }
}
