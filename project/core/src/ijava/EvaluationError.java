// EvaluationException.java
//

package ijava;

/**
 * Exception raised explicitly to indicate an error, i.e. where only the message and not the
 * stack trace should be displayed.
 */
@SuppressWarnings("serial")
public final class EvaluationError extends Exception {

  public EvaluationError(String message) {
    super(message);
  }

  public EvaluationError(String message, Throwable cause) {
    super(message, cause);
  }
}
