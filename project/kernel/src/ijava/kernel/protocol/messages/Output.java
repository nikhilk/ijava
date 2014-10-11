// Output.java
//

package ijava.kernel.protocol.messages;

import java.util.*;
import org.json.simple.*;

import ijava.data.*;
import ijava.kernel.protocol.*;

/**
 * Represents functionality around outgoing output messages.
 */
public final class Output {

  private Output() {
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
    @SuppressWarnings("unchecked")
    public DataMessage(String identity, JSONObject parentHeader, Map<String, String> data) {
      super(identity, Message.DisplayData, parentHeader);

      JSONObject dataObject = new JSONObject();
      for (Map.Entry<String, String> entry : data.entrySet()) {
        dataObject.put(entry.getKey(), entry.getValue());
      }

      JSONObject content = getContent();
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
    @SuppressWarnings("unchecked")
    public StreamMessage(String identity, JSONObject parentHeader, String streamName, String data) {
      super(identity, Message.Stream, parentHeader);

      JSONObject content = getContent();
      content.put("name", streamName);
      content.put("data", data);
    }
  }
}
