// DataMessaage.java
//

package ijava.kernel.protocol.messages;

import java.util.*;
import org.json.simple.*;
import ijava.kernel.protocol.*;

/**
 * Represents the messages used to display data as a result of an execution.
 */
public final class DataMessage extends Message {

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
