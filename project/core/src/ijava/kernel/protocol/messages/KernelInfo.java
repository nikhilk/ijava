// KernalInfo.java
//

package ijava.kernel.protocol.messages;

import org.json.simple.*;

import ijava.kernel.protocol.*;

/**
 * Represents functionality around kernel information requests.
 */
public final class KernelInfo {

  private KernelInfo() {
  }

  /**
   * Represents the kernel_info_request message to request kernel metadata.
   */
  public static final class RequestMessage extends Message {

    /**
     * Creates an instance of a RequestMessage.
     * @param identity the identity of the client.
     * @param header the header of the message.
     * @param parentHeader the header of the associated parent message.
     * @param metadata any metadata associated with the message.
     * @param content the content of the message.
     */
    public RequestMessage(String identity,
                          JSONObject header, JSONObject parentHeader, JSONObject metadata,
                          JSONObject content) {
      super(identity, header, parentHeader, metadata, content);
    }
  }

  /**
   * Represents the kernel_info_reply message with metadata about the kernel.
   */
  public static final class ResponseMessage extends Message {

    /**
     * Creates an instance of a ResponseMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     */
    @SuppressWarnings("unchecked")
    public ResponseMessage(String identity, JSONObject parentHeader) {
      super(identity, Message.KernelInfoResponse, parentHeader);

      JSONArray protocolVersion = new JSONArray();
      protocolVersion.add(new Integer(4));
      protocolVersion.add(new Integer(1));

      JSONArray languageVersion = new JSONArray();
      languageVersion.add(new Integer(1));
      languageVersion.add(new Integer(7));

      JSONObject content = getContent();
      content.put("language", "java");
      content.put("language_version", languageVersion);
      content.put("protocol_version", protocolVersion);
    }
  }

  /**
   * Handles requests for kernel information.
   */
  public static final class Handler implements MessageHandler {

    /**
     * {@link MessageHandler}
     */
    @Override
    public void handleMessage(Message message, MessageServices services) {
      ResponseMessage responseMessage = new ResponseMessage(message.getIdentity(),
                                                            message.getHeader());
      services.sendMessage(responseMessage.associateChannel(message.getChannel()));
    }
  }


  /**
   * Represents the message for communicating kernel status.
   */
  public static final class StatusMessage extends Message {

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
}
