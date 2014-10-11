// Execute.java
//

package ijava.kernel.protocol.messages;

import org.json.simple.*;

import ijava.kernel.protocol.*;

/**
 * Represents functionality around code execution requests.
 */
public final class Execute {

  private Execute() {
  }

  /**
   * Represents the execute_request message to request the kernel to execute code.
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

    public boolean allowInput() {
      return (Boolean)getContent().get("allow_stdin");
    }

    public String getCode() {
      String code = (String)getContent().get("code");
      if (code == null) {
        return "";
      }

      return code;
    }

    public boolean silent() {
      return (Boolean)getContent().get("silent");
    }

    public boolean storeHistory() {
      return (Boolean)getContent().get("store_history");
    }
  }

  /**
   * Represents the execute_reply message to return information about an execution.
   */
  public static abstract class ResponseMessage extends Message {

    /**
     * Indicates a successful execution.
     */
    public static final String SuccessStatus = "ok";

    /**
     * Indicates a failed execution.
     */
    public static final String ErrorStatus = "error";

    /**
     * Indicates an aborted execution.
     */
    public static final String AbortStatus = "abort";

    /**
     * Creates an instance of a ResponseMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     */
    @SuppressWarnings("unchecked")
    public ResponseMessage(String identity, JSONObject parentHeader,
                           String status, int executionCount) {
      super(identity, Message.ExecuteResponse, parentHeader);

      JSONObject content = getContent();
      content.put("status", status);
      content.put("execution_count", new Integer(executionCount));
    }
  }

  /**
   * Represents the execute_reply message for a successful execution.
   */
  public static final class SuccessResponseMessage extends ResponseMessage {

    /**
     * Creates an instance of an SuccessResponseMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     */
    @SuppressWarnings("unchecked")
    public SuccessResponseMessage(String identity, JSONObject parentHeader, int executionCount) {
      super(identity, parentHeader, ResponseMessage.SuccessStatus, executionCount);

      JSONObject content = getContent();
      content.put("payload", new JSONArray());
      content.put("user_variables", new JSONObject());
      content.put("user_expressions", new JSONObject());
    }
  }

  /**
   * Represents the execute_reply message for a failed execution.
   */
  public static final class ErrorResponseMessage extends ResponseMessage {

    /**
     * Creates an instance of an ErrorResponseMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     * @param error the exception that caused the failure.
     */
    @SuppressWarnings("unchecked")
    public ErrorResponseMessage(String identity, JSONObject parentHeader, int executionCount,
                                Exception error) {
      super(identity, parentHeader, ResponseMessage.ErrorStatus, executionCount);

      JSONArray traceback = new JSONArray();
      for (StackTraceElement stackFrame : error.getStackTrace()) {
        String frame = stackFrame.getClassName() + "." + stackFrame.getMethodName() +
            ":" + stackFrame.getLineNumber();

        traceback.add(frame);
      }

      JSONObject content = getContent();
      content.put("ename", error.getClass().getName());
      content.put("evalue", error.getMessage());
      content.put("traceback", traceback);
    }
  }

  /**
   * Represents the execute_reply message for an aborted execution.
   */
  public static final class AbortResponseMessage extends ResponseMessage {

    /**
     * Creates an instance of a AbortResponseMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     */
    public AbortResponseMessage(String identity, JSONObject parentHeader, int executionCount) {
      super(identity, parentHeader, ResponseMessage.AbortStatus, executionCount);
    }
  }

  /**
   * Handles execute requests.
   */
  public static final class Handler implements MessageHandler {

    /**
     * {@link MessageHandler}
     */
    @Override
    public void handleMessage(Message message, MessageServices services) {
      RequestMessage requestMessage = (RequestMessage)message;
      services.processTask(requestMessage.getCode(), requestMessage);
    }
  }
}
