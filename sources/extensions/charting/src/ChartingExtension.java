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
    shell.registerCommand("chart", new ChartCommand());
    return null;
  }
}
