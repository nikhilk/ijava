// Messages.java
//

package ijava.kernel.protocol;

import java.util.*;

/**
 * The message types representing messages received and sent to/from the kernel.
 */
public final class Messages {

  private Messages() {
  }

  /**
   * Represents the kernel_info_request message to request kernel metadata.
   */
  public static final class KernelInfoRequest extends Message {

    /**
     * Creates an instance of a KernelInfoRequest.
     * @param identity the identity of the client.
     * @param header the header of the message.
     * @param parentHeader the header of the associated parent message.
     * @param metadata any metadata associated with the message.
     * @param content the content of the message.
     */
    public KernelInfoRequest(String identity,
                             Map<String, Object> header,
                             Map<String, Object> parentHeader,
                             Map<String, Object> metadata,
                             Map<String, Object> content) {
      super(identity, header, parentHeader, metadata, content);
    }
  }

  /**
   * Represents the kernel_info_reply message with metadata about the kernel.
   */
  public static final class KernelInfoResponse extends Message {

    /**
     * Creates an instance of a KernelInfoResponse.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     */
    public KernelInfoResponse(String identity, Map<String, Object> parentHeader) {
      super(identity, Message.KernelInfoResponse, parentHeader);

      List<Integer> protocolVersion = new ArrayList<Integer>();
      protocolVersion.add(new Integer(4));
      protocolVersion.add(new Integer(1));

      List<Integer> languageVersion = new ArrayList<Integer>();
      languageVersion.add(new Integer(1));
      languageVersion.add(new Integer(7));

      Map<String, Object> content = getContent();
      content.put("language", "java");
      content.put("language_version", languageVersion);
      content.put("protocol_version", protocolVersion);
    }
  }

  /**
   * Represents the message for communicating kernel status.
   */
  public static final class KernelStatus extends Message {

    private static final String IdleStatus = "idle";
    private static final String BusyStatus = "busy";

    /**
     * Creates an instance of a StatusMessage with the specified status.
     * @param status the status of the kernel.
     */
    private KernelStatus(String status) {
      super(null, Message.Status, new HashMap<String, Object>());

      Map<String, Object> content = getContent();
      content.put("execution_state", status);
    }

    /**
     * Creates a status message indicating busy status.
     * @return the message that is ready be published to the client.
     */
    public static Message createBusyStatus() {
      return new KernelStatus(KernelStatus.BusyStatus).associateChannel(MessageChannel.Output);
    }

    /**
     * Creates a status message indicating idle status.
     * @return the message that is ready be published to the client.
     */
    public static Message createIdleStatus() {
      return new KernelStatus(KernelStatus.IdleStatus).associateChannel(MessageChannel.Output);
    }
  }

  /**
   * Represents the shutdown_request message to shutdown a kernel.
   */
  public static final class ShutdownRequest extends Message {

    /**
     * Creates an instance of a ShutdownRequest.
     * @param identity the identity of the client.
     * @param header the header of the message.
     * @param parentHeader the header of the associated parent message.
     * @param metadata any metadata associated with the message.
     * @param content the content of the message.
     */
    public ShutdownRequest(String identity,
                           Map<String, Object> header,
                           Map<String, Object> parentHeader,
                           Map<String, Object> metadata,
                           Map<String, Object> content) {
      super(identity, header, parentHeader, metadata, content);
    }

    public Boolean restart() {
      return (Boolean)getContent().get("restart");
    }
  }

  /**
   * Represents the shutdown_reply message.
   */
  public static final class ShutdownResponse extends Message {

    /**
     * Creates an instance of a ShutdownResponse.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param restart whether the kernel is shutting down for restarting.
     */
    public ShutdownResponse(String identity, Map<String, Object> parentHeader, Boolean restart) {
      super(identity, Message.ShutdownResponse, parentHeader);

      getContent().put("restart", restart);
    }
  }

  /**
   * Represents the execute_request message to request the kernel to execute code.
   */
  public static final class ExecuteRequest extends Message {

    /**
     * Creates an instance of a ExecuteRequest.
     * @param identity the identity of the client.
     * @param header the header of the message.
     * @param parentHeader the header of the associated parent message.
     * @param metadata any metadata associated with the message.
     * @param content the content of the message.
     */
    public ExecuteRequest(String identity,
                          Map<String, Object> header,
                          Map<String, Object> parentHeader,
                          Map<String, Object> metadata,
                          Map<String, Object> content) {
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
  public static abstract class ExecuteResponse extends Message {

    public static final String SuccessStatus = "ok";
    public static final String ErrorStatus = "error";
    public static final String AbortStatus = "abort";

    /**
     * Creates an instance of a ExecuteResponse.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     * @param metadata any additional data associated with the message.
     */
    protected ExecuteResponse(String identity, Map<String, Object> parentHeader,
                              String status, int executionCount) {
      this(identity, parentHeader, status, executionCount, null);
    }

    /**
     * Creates an instance of a ExecuteResponse.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     * @param metadata any additional data associated with the message.
     */
    protected ExecuteResponse(String identity, Map<String, Object> parentHeader,
                              String status, int executionCount,
                              Map<String, Object> metadata) {
      super(identity, Message.ExecuteResponse, parentHeader, metadata);

      Map<String, Object> content = getContent();
      content.put("status", status);
      content.put("execution_count", new Integer(executionCount));
    }
  }

  /**
   * Represents the execute_reply message for a successful execution.
   */
  public static final class SuccessExecuteResponse extends ExecuteResponse {

    /**
     * Creates an instance of an SuccessResponseMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     * @param metadata any additional data associated with the message.
     */
    public SuccessExecuteResponse(String identity,
                                  Map<String, Object> parentHeader,
                                  int executionCount,
                                  Map<String, Object> metadata) {
      super(identity, parentHeader, Messages.ExecuteResponse.SuccessStatus, executionCount,
            metadata);

      Map<String, Object> content = getContent();
      content.put("payload", new ArrayList<Object>());
      content.put("user_variables", new HashMap<String, Object>());
      content.put("user_expressions", new HashMap<String, Object>());
    }
  }

  /**
   * Represents the execute_reply message for a failed execution.
   */
  public static final class ErrorExecuteResponse extends ExecuteResponse {

    /**
     * Creates an instance of an ErrorExecuteResponse.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     * @param error the exception that caused the failure.
     */
    public ErrorExecuteResponse(String identity,
                                Map<String, Object> parentHeader,
                                int executionCount,
                                Throwable error) {
      super(identity, parentHeader, Messages.ExecuteResponse.ErrorStatus, executionCount);

      List<String> traceback = new ArrayList<String>();
      for (StackTraceElement stackFrame : error.getStackTrace()) {
        String frame = stackFrame.getClassName() + "." + stackFrame.getMethodName() +
            ":" + stackFrame.getLineNumber();

        traceback.add(frame);
      }

      Map<String, Object> content = getContent();
      content.put("ename", error.getClass().getName());
      content.put("evalue", error.getMessage());
      content.put("traceback", traceback);
    }
  }

  /**
   * Represents the execute_reply message for an aborted execution.
   */
  public static final class AbortExecuteResponse extends ExecuteResponse {

    /**
     * Creates an instance of a AbortExecuteResponse.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param status the status of the execution.
     * @param executionCount the counter representing the execution sequence number.
     */
    public AbortExecuteResponse(String identity,
                                Map<String, Object> parentHeader,
                                int executionCount) {
      super(identity, parentHeader, Messages.ExecuteResponse.AbortStatus, executionCount);
    }
  }

  /**
   * Represents the messages used to display data as a result of an execution.
   */
  public static final class DataMessage extends Message {

    /**
     * Creates an instance of a DataMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param data the resulting display data as mime/value pairs.
     */
    public DataMessage(String identity,
                       Map<String, Object> parentHeader,
                       Map<String, String> data) {
      super(identity, Message.DisplayData, parentHeader);

      Map<String, Object> dataObject = new HashMap<String, Object>();
      for (Map.Entry<String, String> entry : data.entrySet()) {
        dataObject.put(entry.getKey(), entry.getValue());
      }

      Map<String, Object> content = getContent();
      content.put("data", dataObject);
    }
  }

  /**
   * Represents the messages used to display stream output during execution.
   */
  public static final class StreamMessage extends Message {

    public final static String STDOUT = "stdout";
    public final static String STDERR = "stderr";

    /**
     * Creates an instance of a StreamMessage.
     * @param identity the identity of the client.
     * @param parentHeader the header of the associated parent message.
     * @param streamName the specific output stream.
     * @param data the output content.
     */
    public StreamMessage(String identity,
                         Map<String, Object> parentHeader,
                         String streamName,
                         String data) {
      super(identity, Message.Stream, parentHeader);

      Map<String, Object> content = getContent();
      content.put("name", streamName);
      content.put("data", data);
    }
  }
}
