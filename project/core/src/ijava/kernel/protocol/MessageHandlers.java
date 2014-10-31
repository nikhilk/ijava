// MessageHandlers.java
//

package ijava.kernel.protocol;

/**
 * The set of message handlers implementing the message handling logic in the kernel.
 */
public final class MessageHandlers {

  private MessageHandlers() {
  }

  /**
   * Handles execute requests.
   */
  public static final class ExecuteHandler implements MessageHandler {

    /**
     * {@link MessageHandler}
     */
    @Override
    public void handleMessage(Message message, MessageServices services) {
      Messages.ExecuteRequest request = (Messages.ExecuteRequest)message;
      services.processTask(request.getCode(), request);
    }
  }

  /**
   * Handles requests for kernel information.
   */
  public static final class KernelInfoHandler implements MessageHandler {

    /**
     * {@link MessageHandler}
     */
    @Override
    public void handleMessage(Message message, MessageServices services) {
      Messages.KernelInfoResponse response =
          new Messages.KernelInfoResponse(message.getIdentity(),
                                          message.getHeader());
      services.sendMessage(response.associateChannel(message.getChannel()));
    }
  }

  /**
   * Handles requests for kernel shutdown.
   */
  public static final class ShutdownHandler implements MessageHandler {

    /**
     * {@link MessageHandler}
     */
    @Override
    public void handleMessage(Message message, MessageServices services) {
      Boolean restart = ((Messages.ShutdownRequest)message).restart();
      Messages.ShutdownResponse response =
          new Messages.ShutdownResponse(message.getIdentity(),
                                        message.getHeader(),
                                        restart);
      services.sendMessage(response.associateChannel(message.getChannel()));
      services.endSession();
    }
  }
}
