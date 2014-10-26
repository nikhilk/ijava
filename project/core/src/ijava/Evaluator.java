// Evaluator.java
//

package ijava;

/**
 * A generic evaluation function contract with input data and a result.
 */
public interface Evaluator {

  /**
   * Invokes the evaluator.
   * @param data the input data to be evaluated.
   * @param evaluationID the evaluation sequence number.
   * @return the result of evaluation if any.
   * @throws Exception if there is an evaluation error.
   */
  Object evaluate(String data, int evaluationID) throws Exception;
}
