// CommandOptions.java
//

package ijava.extensibility;

import com.beust.jcommander.*;

public class CommandOptions {

  private String[] _arguments;
  private String _content;
  private String _command;

  @Parameter(names = "--help", description = "Show usage information", help = true)
  public boolean help;

  public final String[] getArguments() {
    return _arguments;
  }

  public final String getCommand() {
    return _command;
  }

  public final void setCommand(String command) {
    _command = command;
  }

  public final String getContent() {
    return _content;
  }

  public JCommander createParser(String name, String[] arguments, String content) {
    _arguments = arguments;
    _content = content;

    JCommander parser = new JCommander(this);
    parser.setProgramName(name);

    return parser;
  }
}
