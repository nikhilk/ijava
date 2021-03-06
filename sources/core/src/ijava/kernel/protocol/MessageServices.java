// MessageServices.java
//

package ijava.kernel.protocol;

import java.util.*;

/**
 * Routes a message that needs to be sent to the kernel client.
 */
public interface MessageServices {

  /**
   * Ends the current session, and shuts down the process.
   */
  public void endSession();

  /**
   * Formats an object into its display data representation.
   * @param data the data to be formatted.
   * @return the display representation of the data keyed by mime types.
   */
  public Map<String, String> formatDisplayData(Object data);

  /**
   * Processes the specified task within the session from input via the specified message.
   * @param content the text defining the task to be performed.
   * @param silent whether to generate any output or not.
   * @param record whether to record the task.
   * @param message the message resulting in the task.
   */
  public void processTask(String content, boolean silent, boolean record, Message message);

  /**
   * Sends a message to the specified message channel.
   * @param message the message to be sent.
   */
  public void sendMessage(Message message);
}
