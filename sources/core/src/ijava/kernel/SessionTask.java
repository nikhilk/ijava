// SessionTask.java
//

package ijava.kernel;

import ijava.kernel.protocol.*;

/**
 * Represents a task that is processed within the context of a kernel session.
 */
public final class SessionTask {

  private final String _content;
  private final boolean _silent;
  private final boolean _record;
  private final Message _message;

  /**
   * Initializes an instance of a SessionTask.
   * @param content the text defining the task to execute.
   * @param silent whether to generate any output or not.
   * @param record whether to record the task.
   * @param message the message originating the task.
   */
  public SessionTask(String content, boolean silent, boolean record, Message message) {
    _content = content;
    _silent = silent;
    _record = record;
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

  /**
   * Whether to record the task processing or not.
   * @return true if the task should be recorded.
   */
  public boolean recordProcessing() {
    return _record;
  }

  /**
   * Whether to process the task silently or not.
   * @return true if the task should be processed silently.
   */
  public boolean requiresSilentProcessing() {
    return _silent;
  }
}
