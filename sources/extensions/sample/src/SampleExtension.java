// SampleExtension.java

import ijava.extensibility.*;

public final class SampleExtension implements ShellExtension {

  @Override
  public Object initialize(Shell shell) {
    shell.declareVariable("sample", "String");
    shell.setVariable("sample", "Hello World!");

    return null;
  }
}
