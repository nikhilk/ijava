// Channel.java
//

package ijava.kernel;

import org.zeromq.ZMQ.*;

/**
 * Represents a channel of communication over a single socket.
 */
final class Channel {

  private final Socket _socket;

  /**
   * Creates an instance of a Channel object for a given socket.
   * @param socket the socket to manage.
   */
  public Channel(Socket socket) {
    _socket = socket;
  }

  /**
   * Gets a reference to the underlying socket.
   * @return the socket object that this channel is associated with.
   */
  public Socket getSocket() {
    return _socket;
  }

  /**
   * Closes the channel and the underlying socket.
   */
  public void close() {
    _socket.close();
  }

  private Message receiveMessage() {
    return null;
  }

  private void sendMessage(Message message) {
  }
}
