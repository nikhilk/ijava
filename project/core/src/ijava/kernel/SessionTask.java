// SessionTask.java
//

package ijava.kernel;

import ijava.kernel.protocol.*;

/**
 * Represents a task that is processed within the context of a kernel session.
 */
public final class SessionTask {

  private final String _content;
  private final Message _message;

  /**
   * Initializes an instance of a SessionTask.
   * @param content the text defining the task to execute.
   * @param message the message originating the task.
   */
  public SessionTask(String content, Message message) {
    _content = content;
    _message = message;
  }

  /**
   * Gets the text associated with the task.
   * @return the task content.
   */
  public String getContent() {
    return _content;
  }

  /**
   * Gets the message associated with this task.
   * @return the associated message object.
   */
  public Message getMessage() {
    return _message;
  }
}
