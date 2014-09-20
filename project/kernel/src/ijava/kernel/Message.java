// Message.java
//

package ijava.kernel;

import org.json.simple.*;

/**
 * Represents a single message that is received or sent over a channel.
 */
public abstract class Message {

  /**
   * A request for information about the kernel.
   */
  public final static String KernelInfoRequest = "kernel_info_request";

  /**
   * A response with information about the kernel.
   */
  public final static String KernelInfoResponse = "kernel_info_reply";

  /**
   * A request to execute code within the kernel.
   */
  public final static String ExecuteRequest = "execute_request";

  /**
   * A response with status about an execution.
   */
  public final static String ExecuteResponse = "execute_reply";

  /**
   * A message that publishes the status of the kernel.
   */
  public final static String Status = "status";

  /**
   * A message that publishes display data resulting from an execution.
   */
  public final static String DisplayData = "display_data";

  /**
   * A message that publishes stream output during an execution.
   */
  public final static String Stream = "stream";


  private final JSONObject _header;
  private final JSONObject _parentHeader;
  private final JSONObject _metadata;
  private final JSONObject _content;

  Message(String id, String type, JSONObject parentHeader) {
    this(Message.createHeader(id, type), parentHeader, new JSONObject(), new JSONObject());
  }

  Message(JSONObject header, JSONObject parentHeader, JSONObject metadata, JSONObject content) {
    _header = header;
    _parentHeader = parentHeader;
    _metadata = metadata;
    _content = content;
  }

  JSONObject getContent() {
    return _content;
  }

  JSONObject getHeader() {
    return _header;
  }

  /**
   * Gets the unique id of the message.
   * @return The UUID representing the message id.
   */
  public String getId() {
    return (String)_header.get("msg_id");
  }

  JSONObject getMetadata() {
    return _metadata;
  }

  JSONObject getParentHeader() {
    return _parentHeader;
  }

  /**
   * Gets the type of the message.
   * @return The type name of the message.
   */
  public String getType() {
    return (String)_header.get("msg_type");
  }

  @SuppressWarnings("unchecked")
  private static JSONObject createHeader(String id, String type) {
    JSONObject header = new JSONObject();

    header.put("msg_id", id);
    header.put("msg_type", type);
    return header;
  }
}
