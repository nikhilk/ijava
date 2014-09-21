// Channel.java
//

package ijava.kernel;

import org.zeromq.*;
import org.zeromq.ZMQ.*;
import ijava.kernel.protocol.*;

/**
 * Represents a channel of communication over a single socket.
 */
final class Channel {

  private final MessageChannel _channelType;
  private final Socket _socket;

  /**
   * Creates an instance of a Channel object for a given socket.
   * @param channelType the type of message channel this represents.
   * @param socket the socket to manage.
   */
  public Channel(MessageChannel channelType, Socket socket) {
    _channelType = channelType;
    _socket = socket;
  }

  /**
   * Gets the channel type assigned to this channel.
   * @return the associated channel type.
   */
  public MessageChannel getChannelType() {
    return _channelType;
  }

  /**
   * Gets the underlying socket.
   * @return the associated socket.
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

  /**
   * Reads the current message from the underlying socket.
   * @return The received message.
   */
  public Message receiveMessage() {
    return MessageIO.readMessage(_socket);
  }

  /**
   * Writes the specified message to the underlying socket.
   * @param message The message to be sent.
   */
  public void sendMessage(Message message) {
    MessageIO.writeMessage(_socket, message);
  }
}
