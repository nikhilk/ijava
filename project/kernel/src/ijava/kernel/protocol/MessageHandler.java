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
   * @param source the originating channel on which this message was received.
   * @param services services that can be used to process the message.
   */
  public void handleMessage(Message message, MessageChannel source, MessageServices services);
}
