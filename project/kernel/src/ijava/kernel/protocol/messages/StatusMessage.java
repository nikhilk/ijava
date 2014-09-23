// StatusMessage.java
//

package ijava.kernel.protocol.messages;

import org.json.simple.*;
import ijava.kernel.protocol.*;

/**
 * Represents the message for communicating kernel status.
 */
public final class StatusMessage extends Message {

  /**
   * Indicates the kernel is starting.
   */
  public static final String StartingStatus = "starting";

  /**
   * Indicates the kernel is idle.
   */
  public static final String IdleStatus = "idle";

  /**
   * Indicates the kernel is busy executing code.
   */
  public static final String BusyStatus = "busy";

  /**
   * Creates an instance of a StatusMessage with the specified status.
   * @param status the status of the kernel.
   */
  @SuppressWarnings("unchecked")
  public StatusMessage(String status) {
    super(null, Message.Status, new JSONObject());

    JSONObject content = getContent();
    content.put("execution_state", status);
  }
}
