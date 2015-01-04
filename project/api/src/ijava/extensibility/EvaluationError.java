// EvaluationError.java
//

package ijava.extensibility;

/**
 * Exception raised explicitly to indicate an error, i.e. where only the message and not the
 * stack trace should be displayed.
 */
@SuppressWarnings("serial")
public final class EvaluationError extends Exception {

  /**
   * Creates an EvaluationError with the specified message.
   * @param message the error message.
   */
  public EvaluationError(String message) {
    super(message);
  }

  /**
   * Creates an EvaluationError with the specified message as a result of the specified
   * exception.
   * @param message the error message.
   * @param cause the exception causing the error.
   */
  public EvaluationError(String message, Throwable cause) {
    super(message, cause);
  }
}
