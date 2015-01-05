// Evaluator.java
//

package ijava.extensibility;

import java.util.*;

/**
 * A generic evaluation function contract with input data, associated metadata and a result.
 */
public interface Evaluator {

  /**
   * Invokes the evaluator.
   * @param data the input data to be evaluated.
   * @param evaluationID the evaluation sequence number.
   * @param metadata any metadata associated with the evaluation.
   * @return the result of evaluation if any.
   * @throws Exception if there is an evaluation error.
   */
  Object evaluate(String data, long evaluationID, Map<String, Object> metadata) throws Exception;
}
