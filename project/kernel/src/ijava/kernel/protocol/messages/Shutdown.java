// Shutdown.java
//

package ijava.kernel.protocol.messages;

import org.json.simple.*;
import ijava.kernel.protocol.*;

/**
 * Represents functionality around shutdown requests.
 */
public final class Shutdown {

  private Shutdown() {
  }

  /**
   * Represents the shutdown_request message to shutdown a kernel.
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

    public Boolean restart() {
      return (Boolean)getContent().get("restart");
    }
  }

  /**
   * Represents the shutdown_reply message.
   */
  public static final class ResponseMessage extends Message {

    /**
     * Creates an instance of a ResponseMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param restart whether the kernel is shutting down for restarting.
     */
    @SuppressWarnings("unchecked")
    public ResponseMessage(String identity, JSONObject parentHeader, Boolean restart) {
      super(identity, Message.ShutdownResponse, parentHeader);

      getContent().put("restart", restart);
    }
  }

  /**
   * Handles requests for kernel shutdown.
   */
  public static final class Handler implements MessageHandler {

    /**
     * {@link MessageHandler}
     */
    @Override
    public void handleMessage(Message message, MessageChannel source, MessageServices services) {
      Boolean restart = ((RequestMessage)message).restart();
      ResponseMessage responseMessage = new ResponseMessage(message.getIdentity(),
                                                            message.getHeader(),
                                                            restart);
      services.sendMessage(responseMessage, source);
      services.endSession();
    }
  }
}
