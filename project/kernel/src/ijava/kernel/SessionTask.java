// SessionTask.java
//

package ijava.kernel;

import ijava.kernel.protocol.*;

/**
 * Represents a task that is processed within the context of a kernel session.
 */
public final class SessionTask {

  private final String _text;
  private final Message _message;

  /**
   * Initializes an instance of a SessionTask.
   * @param text the description of the task.
   * @param message the message originating the task.
   */
  public SessionTask(String text, Message message) {
    _text = text;
    _message = message;
  }
}
