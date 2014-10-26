// MessageHandler.java
//

package ijava.kernel.protocol;

/**
 * Handles incoming messages to process them as appropriate.
 */
public interface MessageHandler {

  /**
   * Handles the specified message.
   * @param message the message to process.
   * @param services services that can be used to process the message.
   */
  public void handleMessage(Message message, MessageServices services);
}
