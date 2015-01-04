// Command.java
//

package ijava.shell;

/**
 * Implemented by shell commands to handle evaluation requests.
 */
public interface Command {

  /**
   * Evaluates the command to perform its associated action.
   * @param shell the shell that this command is registered with.
   * @param arguments the set of arguments passed to the command.
   * @param evaluationID the evaluation sequence number.
   * @param content the optional remaining data to be used during command evaluation.
   * @return an optional object result.
   * @throws Exception if there is an error during evaluation.
   */
  public Object evaluate(InteractiveShell shell, int evaluationID,
                         String declaration, String content) throws Exception;
}
