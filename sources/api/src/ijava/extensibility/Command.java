// Command.java
//

package ijava.extensibility;

import java.util.*;

/**
 * Implemented by shell commands to handle evaluation requests.
 */
public interface Command {

  /**
   * Evaluates the command to perform its associated action.
   * @param arguments the set of arguments passed to the command.
   * @param data the optional remaining data to be used during command evaluation.
   * @param evaluationID the evaluation sequence number.
   * @param metadata any metadata associated with the evaluation.
   * @return an optional object result.
   * @throws Exception if there is an error during evaluation.
   */
  public Object evaluate(String arguments, String data, long evaluationID,
                         Map<String, Object> metadata) throws Exception;
}
