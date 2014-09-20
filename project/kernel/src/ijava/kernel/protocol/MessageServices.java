// MessageServices.java
//

package ijava.kernel.protocol;

/**
 * Routes a message that needs to be sent to the kernel client.
 */
public interface MessageServices {

  public void sendMessage(Message message, MessageChannel target);
}
