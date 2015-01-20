// Command.java
//

package ijava.extensibility;

import java.util.*;

/**
 * Implemented by shell commands to handle evaluation requests.
 */
public abstract class Command<TOptions extends CommandOptions> {

  private final Shell _shell;
  private final Class<TOptions> _optionsClass;
  private final boolean _singleLine;

  /**
   * Initializes a new Command instance as a single line command.
   * @param shell the Shell instance associated with the command.
   * @param optionsClass the type of the options class.
   */
  protected Command(Shell shell, Class<TOptions> optionsClass) {
    this(shell, optionsClass, /* singleLine */ true);
  }

  /**
   * Initializes a new Command instance.
   * @param shell the Shell instance associated with the command.
   * @param optionsClass the type of the options class.
   * @param singleLine whether the command is a single line of arguments or accepts content.
   */
  protected Command(Shell shell, Class<TOptions> optionsClass, boolean singleLine) {
    _shell = shell;
    _optionsClass = optionsClass;
    _singleLine = singleLine;
  }

  /**
   * Gets the Shell instance that is associated with the command.
   * @return the current Shell object.
   */
  protected final Shell getShell() {
    return _shell;
  }

  /**
   * Indicates whether the command is a single line of arguments or also accepts content.
   * @return true if the command spans a single line, and false otherwise.
   */
  public final boolean isSingleLine() {
    return _singleLine;
  }

  /**
   * Creates the options object to hold any command arguments or content.
   * @return a new instance of an options object.
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public final TOptions createOptions() throws InstantiationException, IllegalAccessException {
    return _optionsClass.newInstance();
  }

  /**
   * Evaluates the command to perform its associated action.
   * @param options the input arguments and content provided.
   * @param evaluationID the evaluation sequence number.
   * @param metadata any metadata associated with the evaluation.
   * @return an optional object result.
   * @throws Exception if there is an error during evaluation.
   */
  public abstract Object evaluate(TOptions options, long evaluationID,
                                  Map<String, Object> metadata) throws Exception;


  /**
   * Base class for simple commands - single line commands with no options.
   */
  public static abstract class SimpleCommand extends Command<CommandOptions> {

    protected SimpleCommand(Shell shell) {
      super(shell, CommandOptions.class);
    }

    /**
     * Evaluates the command to perform its associated action.
     * @param evaluationID the evaluation sequence number.
     * @param metadata any metadata associated with the evaluation.
     * @return an optional object result.
     * @throws Exception if there is an error during evaluation.
     */
    protected abstract Object evaluate(long evaluationID,
                                       Map<String, Object> metadata) throws Exception;

    @Override
    public final Object evaluate(CommandOptions options, long evaluationID,
                                 Map<String, Object> metadata) throws Exception {
      return evaluate(evaluationID, metadata);
    }
  }

  /**
   * Base class for content only commands.
   */
  public static abstract class ContentCommand extends Command<CommandOptions> {

    protected ContentCommand(Shell shell) {
      super(shell, CommandOptions.class, /* singleLine */ false);
    }

    /**
     * Evaluates the command to perform its associated action.
     * @param content the input content provided.
     * @param evaluationID the evaluation sequence number.
     * @param metadata any metadata associated with the evaluation.
     * @return an optional object result.
     * @throws Exception if there is an error during evaluation.
     */
    public abstract Object evaluate(String content, long evaluationID,
                                    Map<String, Object> metadata) throws Exception;

    @Override
    public final Object evaluate(CommandOptions options, long evaluationID,
                                 Map<String, Object> metadata) throws Exception {
      return evaluate(options.getContent(), evaluationID, metadata);
    }
  }
}
