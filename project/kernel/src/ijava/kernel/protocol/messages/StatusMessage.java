// StatusMessage.java
//

package ijava.kernel.protocol.messages;

import org.json.simple.*;
import ijava.kernel.protocol.*;

/**
 * Represents the message for communicating kernel status.
 */
public final class StatusMessage extends Message {

  private static final String IdleStatus = "idle";
  private static final String BusyStatus = "busy";

  /**
   * Creates an instance of a StatusMessage with the specified status.
   * @param status the status of the kernel.
   */
  @SuppressWarnings("unchecked")
  private StatusMessage(String status) {
    super(null, Message.Status, new JSONObject());

    JSONObject content = getContent();
    content.put("execution_state", status);
  }

  /**
   * Creates a status message indicating busy status.
   * @return the message that is ready be published to the client.
   */
  public static Message createBusyStatus() {
    return new StatusMessage(StatusMessage.BusyStatus).associateChannel(MessageChannel.Output);
  }

  /**
   * Creates a status message indicating idle status.
   * @return the message that is ready be published to the client.
   */
  public static Message createIdleStatus() {
    return new StatusMessage(StatusMessage.IdleStatus).associateChannel(MessageChannel.Output);
  }
}
