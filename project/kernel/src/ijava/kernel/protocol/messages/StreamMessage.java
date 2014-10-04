// StreamMessaage.java
//

package ijava.kernel.protocol.messages;

import org.json.simple.*;
import ijava.kernel.protocol.*;

/**
 * Represents the messages used to display stream output during execution.
 */
public final class StreamMessage extends Message {

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
