// Extension.java
//

package ijava.shell;

/**
 * Implemented by evaluator extensions to handle evaluation requests.
 */
public interface Extension {

  /**
   * Invokes the extension.
   * @param shell the shell that this extension is registered with.
   * @param declaration the first line of the extension invocation.
   * @param evaluationID the evaluation sequence number.
   * @param content the optional remaining data to be evaluated.
   * @return an optional object result.
   * @throws Exception if there is an error during evaluation.
   */
  public Object evaluate(InteractiveShell shell, int evaluationID,
                         String declaration, String content) throws Exception;
}
