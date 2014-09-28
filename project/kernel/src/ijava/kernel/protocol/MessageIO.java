// SocketHandler.java
//

package ijava.kernel.protocol;

import org.json.simple.*;
import org.json.simple.parser.*;
import org.zeromq.ZMQ.*;

/**
 * Implements message reading and writing per the kernel communication protocol.
 */
public final class MessageIO {

  private final static String DELIMITER = "<IDS|MSG>";

  private final static JSONParser Parser = new JSONParser();

  private MessageIO() {
  }

  /**
   * Reads an incoming message from the specified socket.
   * @param socket the socket to read from.
   * @return the resulting Message instance.
   */
  public static Message readMessage(Socket socket) {
    try {
      String identity = null;
      for (String id = socket.recvStr(); !id.equals(MessageIO.DELIMITER); id = socket.recvStr()) {
        if (identity == null) {
          identity = id;
        }

        // TODO: When are there multiple identities?
      }

      String hmac = socket.recvStr();
      String headerJson = socket.recvStr();
      String parentHeaderJson = socket.recvStr();
      String metadataJson = socket.recvStr();
      String contentJson = socket.recvStr();

      // TODO: Verify HMAC

      JSONObject header = (JSONObject)MessageIO.Parser.parse(headerJson);
      JSONObject parentHeader = (JSONObject)MessageIO.Parser.parse(parentHeaderJson);
      JSONObject metadata = (JSONObject)MessageIO.Parser.parse(metadataJson);
      JSONObject content = (JSONObject)MessageIO.Parser.parse(contentJson);

      System.out.println("Read message of type " + header.get("msg_type"));
      return Message.createMessage(identity, header, parentHeader, metadata, content);
    }
    catch (Exception e) {
      // TODO: Logging
      return null;
    }
  }

  /**
   * Writes an out-going message to the specified socket.
   * @param socket the socket to write to.
   * @param message the message to send.
   */
  public static void writeMessage(Socket socket, Message message) {
    String identity = message.getIdentity();
    String headerJson = message.getHeader().toJSONString();
    String parentHeaderJson = message.getParentHeader().toJSONString();
    String metadataJson = message.getMetadata().toJSONString();
    String contentJson = message.getContent().toJSONString();

    // TODO: Compute HMAC
    String hmac = "";

    if (identity != null) {
      socket.sendMore(identity);
    }

    socket.sendMore(MessageIO.DELIMITER);
    socket.sendMore(hmac);
    socket.sendMore(headerJson);
    socket.sendMore(parentHeaderJson);
    socket.sendMore(metadataJson);
    socket.send(contentJson);
  }
}
