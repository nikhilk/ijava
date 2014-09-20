// Session.java
//

package ijava.kernel;

import org.zeromq.*;
import org.zeromq.ZMQ.*;
import ijava.kernel.protocol.*;

/**
 * Represents a running Kernel instance.
 */
public final class Session implements MessageServices {

  private final static int ZMQ_IO_THREADS = 1;

  private final SessionOptions _options;

  private Context _context;
  private Channel _heartbeatChannel;
  private Channel _controlChannel;
  private Channel _shellChannel;
  private Channel _stdinChannel;
  private Channel _ioPubChannel;

  /**
   * Creates and initializes an instance of a Session object.
   * @param options the options describing the Session instance.
   */
  public Session(SessionOptions options) {
    _options = options;
  }

  private Channel createChannel(MessageChannel channelType, int socketType, int port) {
    Socket socket = _context.socket(socketType);

    String address = String.format("%s://%s:%d", _options.getTransport(), _options.getIP(), port);
    socket.bind(address);

    return new Channel(channelType, socket);
  }

  /**
   * Starts the session.
   */
  public void start() {
    _context = ZMQ.context(Session.ZMQ_IO_THREADS);

    _heartbeatChannel = createChannel(MessageChannel.Heartbeat, ZMQ.REP,
                                      _options.getHeartbeatPort());
    _controlChannel = createChannel(MessageChannel.Requests, ZMQ.ROUTER,
                                    _options.getControlPort());
    _shellChannel = createChannel(MessageChannel.Requests, ZMQ.ROUTER, _options.getShellPort());
    _stdinChannel = createChannel(MessageChannel.Input, ZMQ.ROUTER, _options.getStdinPort());
    _ioPubChannel = createChannel(MessageChannel.Output, ZMQ.PUB, _options.getIOPubPort());

    _heartbeatChannel.echoMessages();
  }

  /**
   * Stops the session.
   */
  public void stop() {
    _ioPubChannel.close();
    _stdinChannel.close();
    _shellChannel.close();
    _controlChannel.close();
    _heartbeatChannel.close();

    _context.term();
  }

  /**
   * {@link MessageServices}
   */
  @Override
  public void sendMessage(Message message, MessageChannel target) {
    // TODO: Implement this
  }
}
