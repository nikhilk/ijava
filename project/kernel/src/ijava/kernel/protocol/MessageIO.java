// SocketHandler.java
//

package ijava.kernel.protocol;

import org.zeromq.ZMQ.*;

/**
 * Implements message reading and writing per the kernel communication protocol.
 */
public final class MessageIO {

  private MessageIO() {
  }

  /**
   * Reads an incoming message from the specified socket.
   * @param socket the socket to read from.
   * @return the resulting Message instance.
   */
  public static Message readMessage(Socket socket) {
    return null;
  }

  /**
   * Writes an out-going message to the specified socket.
   * @param socket the socket to write to.
   * @param message the message to send.
   */
  public static void writeMessage(Socket socket, Message message) {
  }
}
