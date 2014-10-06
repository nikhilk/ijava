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
   * @return the result of evaluation if any.
   */
  Object evaluate(String data);
}
