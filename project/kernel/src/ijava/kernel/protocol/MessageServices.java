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
   * Sends a message to the specified message channel.
   * @param message the message to be sent.
   * @param target the target channel.
   */
  public void sendMessage(Message message, MessageChannel target);
}
