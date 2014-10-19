// EvaluatorExtension.java
//

package ijava;

/**
 * Implemented by evaluator extensions to handle evaluation requests.
 */
public interface EvaluatorExtension {

  /**
   * Invokes the evaluator extension.
   * @param evaluator the evaluator that this extension is registered with.
   * @param declaration the first line of the extension invocation.
   * @param content the optional remaining data to be evaluated.
   * @return an optional object result.
   * @throws Exception if there is an error during evaluation.
   */
  public Object evaluate(Evaluator evaluator, String declaration, String content) throws Exception;
}
