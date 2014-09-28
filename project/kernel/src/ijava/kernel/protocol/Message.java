// Message.java
//

package ijava.kernel.protocol;

import java.lang.reflect.*;
import java.util.*;
import org.json.simple.*;
import ijava.kernel.protocol.messages.*;

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
   * A request to shutdown the kernel.
   */
  public final static String ShutdownRequest = "shutdown_request";

  /**
   * A response with results from a kernel shutdown.
   */
  public final static String ShutdownResponse = "shutdown_reply";

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


  private final static Map<String, Class<? extends Message>> MessageTypes;
  private final static Map<String, Class<? extends MessageHandler>> MessageHandlers;

  private final String _identity;
  private final JSONObject _header;
  private final JSONObject _parentHeader;
  private final JSONObject _metadata;
  private final JSONObject _content;

  static {
    HashMap<String, Class<? extends Message>> messageTypes =
        new HashMap<String, Class<? extends Message>>();
    messageTypes.put(Message.KernelInfoRequest, KernelInfo.RequestMessage.class);
    messageTypes.put(Message.ShutdownRequest, Shutdown.RequestMessage.class);
    messageTypes.put(Message.ExecuteRequest, Execute.RequestMessage.class);

    HashMap<String, Class<? extends MessageHandler>> messageHandlers =
        new HashMap<String, Class<? extends MessageHandler>>();
    messageHandlers.put(Message.KernelInfoRequest, KernelInfo.Handler.class);
    messageHandlers.put(Message.ShutdownRequest, Shutdown.Handler.class);
    messageHandlers.put(Message.ExecuteRequest, Execute.Handler.class);

    MessageTypes = messageTypes;
    MessageHandlers = messageHandlers;
  }

  /**
   * Creates and initializes a Message.
   * @param type the type of the message.
   * @param parentHeader the header of the associated parent message.
   */
  protected Message(String identity, String type, JSONObject parentHeader) {
    this(identity, Message.createHeader(type), parentHeader, new JSONObject(), new JSONObject());
  }

  /**
   * Creates and initializes a message from its constituent parts.
   * @param identity the client identity.
   * @param header the header of the message.
   * @param parentHeader the header of the associated parent message.
   * @param metadata any metadata associated with the message.
   * @param content the content of the message.
   */
  protected Message(String identity,
                    JSONObject header, JSONObject parentHeader, JSONObject metadata,
                    JSONObject content) {
    _identity = identity;
    _header = header;
    _parentHeader = parentHeader;
    _metadata = metadata;
    _content = content;
  }

  /**
   * Creates a message from its constituent parts.
   * @param identity the identity of the client.
   * @param header the header of the message.
   * @param parentHeader the header of the associated parent message.
   * @param metadata any metadata associated with the message.
   * @param content the content of the message.
   * @return a message object of appropriate type.
   */
  public static Message createMessage(String identity,
                                      JSONObject header, JSONObject parentHeader,
                                      JSONObject metadata,
                                      JSONObject content) {
    String type = (String)header.get("msg_type");

    Class<? extends Message> messageClass = Message.MessageTypes.get(type);
    if (messageClass == null) {
      System.out.println("Unknown message type: " + type);
      // TODO: Logging
      return null;
    }

    try {
      Constructor<? extends Message> messageCtor =
          messageClass.getConstructor(String.class,
                                      JSONObject.class, JSONObject.class,
                                      JSONObject.class, JSONObject.class);
      return messageCtor.newInstance(identity, header, parentHeader, metadata, content);
    }
    catch (Exception e) {
      System.out.println("Unhandled message type: " + type);
      // TODO: Logging
      return null;
    }
  }

  /**
   * Gets the content associated with the message.
   * @return the content object.
   */
  protected JSONObject getContent() {
    return _content;
  }

  /**
   * Gets the handler that can process this message.
   * @return the handler if one is registered.
   */
  public MessageHandler getHandler() {
    Class<? extends MessageHandler> handlerClass = Message.MessageHandlers.get(getType());
    if (handlerClass == null) {
      return null;
    }

    try {
      return handlerClass.newInstance();
    }
    catch (Exception e) {
      // TODO: Logging
      return null;
    }
  }

  /**
   * Gets the header of the message.
   * @return the header object.
   */
  public JSONObject getHeader() {
    return _header;
  }

  /**
   * Gets the unique id of the message.
   * @return The UUID representing the message id.
   */
  public String getId() {
    return (String)_header.get("msg_id");
  }

  /**
   * Gets the identity of the client associated with the message.
   * @return the identity string.
   */
  public String getIdentity() {
    return _identity;
  }

  /**
   * Gets the metadata associated with the message.
   * @return the metadata object.
   */
  protected JSONObject getMetadata() {
    return _metadata;
  }

  /**
   * Gets the header of associated parent message.
   * @return the parent header object.
   */
  protected JSONObject getParentHeader() {
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
  private static JSONObject createHeader(String type) {
    String id = UUID.randomUUID().toString().replace("-", "");
    JSONObject header = new JSONObject();

    header.put("msg_id", id);
    header.put("msg_type", type);
    return header;
  }
}
