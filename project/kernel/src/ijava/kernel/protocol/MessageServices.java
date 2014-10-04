// MessageServices.java
//

package ijava.kernel.protocol;

/**
 * Routes a message that needs to be sent to the kernel client.
 */
public interface MessageServices {

  /**
   * Ends the current session, and shuts down the process.
   */
  public void endSession();

  /**
   * Processes the specified task within the session from input via the specified message.
   * @param taskInput the text describing the task to be performed.
   * @param message the message resulting in the task.
   */
  public void processTask(String taskInput, Message message);

  /**
   * Sends a message to the specified message channel.
   * @param message the message to be sent.
   */
  public void sendMessage(Message message);
}
