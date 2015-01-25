// SocketHandler.java
//

package ijava.kernel.protocol;

import java.util.*;
import com.fasterxml.jackson.jr.ob.*;
import ijava.kernel.*;
import org.zeromq.ZMQ.*;

/**
 * Implements message reading and writing per the kernel communication protocol.
 */
public final class MessageIO {

  private final static String DELIMITER = "<IDS|MSG>";

  private MessageIO() {
  }

  /**
   * Reads an incoming message from the specified socket.
   * @param socket the socket to read from.
   * @param signer the message signer to use to validate messages.
   * @return the resulting Message instance.
   */
  public static Message readMessage(Socket socket, MessageSigner signer) {
    String identity = null;
    for (String id = socket.recvStr(); !id.equals(MessageIO.DELIMITER); id = socket.recvStr()) {
      if (identity == null) {
        identity = id;
      }
      else {
        Session.Log.error("Recieved a message with multiple identities (unsupported)");
      }
    }

    String signature = socket.recvStr();
    String headerJson = socket.recvStr();
    String parentHeaderJson = socket.recvStr();
    String metadataJson = socket.recvStr();
    String contentJson = socket.recvStr();

    if (!signer.validate(signature, headerJson, parentHeaderJson, metadataJson, contentJson)) {
      Session.Log.error("Unable to verify message signature");
      return null;
    }

    try {
      Map<String, Object> header = JSON.std.mapFrom(headerJson);
      Map<String, Object> parentHeader = JSON.std.mapFrom(parentHeaderJson);
      Map<String, Object> metadata = JSON.std.mapFrom(metadataJson);
      Map<String, Object> content = JSON.std.mapFrom(contentJson);

      return Message.createMessage(identity, header, parentHeader, metadata, content);
    }
    catch (Exception e) {
      Session.Log.exception(e, "Failed to parse incoming message\n" +
          "Header: %s\nParent Header: %s\nMetadata: %s\nContent: %s",
          headerJson, parentHeaderJson, metadataJson, contentJson);
      return null;
    }
  }

  /**
   * Writes an out-going message to the specified socket.
   * @param socket the socket to write to.
   * @param signer the message signer to use to compute signatures.
   * @param message the message to send.
   */
  public static void writeMessage(Socket socket, MessageSigner signer, Message message) {
    try {
      String identity = message.getIdentity();
      String headerJson = JSON.std.asString(message.getHeader());
      String parentHeaderJson = JSON.std.asString(message.getParentHeader());
      String metadataJson = JSON.std.asString(message.getMetadata());
      String contentJson = JSON.std.asString(message.getContent());
      String signature = signer.signature(headerJson, parentHeaderJson, metadataJson, contentJson);

      if (identity != null) {
        socket.sendMore(identity);
      }

      socket.sendMore(MessageIO.DELIMITER);
      socket.sendMore(signature);
      socket.sendMore(headerJson);
      socket.sendMore(parentHeaderJson);
      socket.sendMore(metadataJson);
      socket.send(contentJson);
    }
    catch (Exception e) {
      Session.Log.exception(e, "Unable to send message of type %s", message.getType());
    }
  }
}
