// Message.java
//

package ijava.kernel.protocol;

import java.lang.reflect.*;
import java.util.*;

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
  private final Map<String, Object> _header;
  private final Map<String, Object> _parentHeader;
  private final Map<String, Object> _metadata;
  private final Map<String, Object> _content;

  private MessageChannel _channel;

  static {
    HashMap<String, Class<? extends Message>> messageTypes =
        new HashMap<String, Class<? extends Message>>();
    messageTypes.put(Message.KernelInfoRequest, Messages.KernelInfoRequest.class);
    messageTypes.put(Message.ShutdownRequest, Messages.ShutdownRequest.class);
    messageTypes.put(Message.ExecuteRequest, Messages.ExecuteRequest.class);

    HashMap<String, Class<? extends MessageHandler>> messageHandlers =
        new HashMap<String, Class<? extends MessageHandler>>();
    messageHandlers.put(Message.KernelInfoRequest, MessageHandlers.KernelInfoHandler.class);
    messageHandlers.put(Message.ShutdownRequest, MessageHandlers.ShutdownHandler.class);
    messageHandlers.put(Message.ExecuteRequest, MessageHandlers.ExecuteHandler.class);

    MessageTypes = messageTypes;
    MessageHandlers = messageHandlers;
  }

  /**
   * Creates and initializes a Message.
   * @param type the type of the message.
   * @param parentHeader the header of the associated parent message.
   */
  protected Message(String identity, String type, Map<String, Object> parentHeader) {
    this(identity, Message.createHeader(type), parentHeader,
         new HashMap<String, Object>(),
         new HashMap<String, Object>());
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
                    Map<String, Object> header,
                    Map<String, Object> parentHeader,
                    Map<String, Object> metadata,
                    Map<String, Object> content) {
    _identity = identity;
    _header = header;
    _parentHeader = parentHeader;
    _metadata = metadata;
    _content = content;
  }

  /**
   * Associates the channel that this message was read from, or needs to be written to.
   * @param channel the associated channel.
   * @return the message with an associated channel.
   */
  public Message associateChannel(MessageChannel channel) {
    _channel = channel;
    return this;
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
                                      Map<String, Object> header,
                                      Map<String, Object> parentHeader,
                                      Map<String, Object> metadata,
                                      Map<String, Object> content) {
    String type = (String)header.get("msg_type");

    Class<? extends Message> messageClass = Message.MessageTypes.get(type);
    if (messageClass == null) {
      System.out.println("Unknown message type: " + type);
      // TODO: Logging
      return null;
    }

    try {
      Constructor<? extends Message> messageCtor =
          messageClass.getConstructor(String.class, Map.class, Map.class, Map.class, Map.class);
      return messageCtor.newInstance(identity, header, parentHeader, metadata, content);
    }
    catch (Exception e) {
      System.out.println("Unhandled message type: " + type);
      // TODO: Logging
      return null;
    }
  }

  /**
   * Gets the channel associated with this message.
   * @return the associated channel.
   */
  public MessageChannel getChannel() {
    return _channel;
  }

  /**
   * Gets the content associated with the message.
   * @return the content object.
   */
  protected Map<String, Object> getContent() {
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
  public Map<String, Object> getHeader() {
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
  protected Map<String, Object> getMetadata() {
    return _metadata;
  }

  /**
   * Gets the header of associated parent message.
   * @return the parent header object.
   */
  protected Map<String, Object> getParentHeader() {
    return _parentHeader;
  }

  /**
   * Gets the type of the message.
   * @return The type name of the message.
   */
  public String getType() {
    return (String)_header.get("msg_type");
  }

  private static Map<String, Object> createHeader(String type) {
    String id = UUID.randomUUID().toString().replace("-", "");
    Map<String, Object> header = new HashMap<String, Object>();

    header.put("msg_id", id);
    header.put("msg_type", type);
    return header;
  }
}
