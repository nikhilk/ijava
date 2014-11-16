// Sample.java
//

package sample;

import ijava.shell.*;

public class Shell extends InteractiveShell {

  public Shell() {
    super();

    InteractiveState state = getState();
    state.declareField("sample", "String");
    state.setValue("sample", "Hello World!");
  }
}

