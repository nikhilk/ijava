// DataMessaage.java
//

package ijava.kernel.protocol.messages;

import java.util.*;
import org.json.simple.*;

import ijava.data.*;
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

  /**
   * Creates a map of mime/formatted data pairs from an instance value.
   * @param value the value to be formatted.
   * @return a map of different mime representations for the specified value.
   */
  public static Map<String, String> createData(Object value) {
    if (value == null) {
      return null;
    }

    if (value instanceof DisplayData) {
      return ((DisplayData)value).toDisplayRepresentations();
    }

    // TODO: Interesting standard Java types to special case?

    // Default to a textual representation produced via toString.
    HashMap<String, String> representations = new HashMap<String, String>();
    representations.put("text/plain", value.toString());

    return representations;
  }
}
