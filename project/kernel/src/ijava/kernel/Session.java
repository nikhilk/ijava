// Session.java
//

package ijava.kernel;

import org.zeromq.*;
import org.zeromq.ZMQ.*;
import ijava.kernel.protocol.*;

/**
 * Represents a running Kernel instance.
 */
public final class Session implements Runnable, MessageServices {

  private final static int ZMQ_IO_THREADS = 1;

  private final SessionOptions _options;

  private Context _context;
  private Channel _heartbeatChannel;
  private Channel _controlChannel;
  private Channel _shellChannel;
  private Channel _stdinChannel;
  private Channel _ioPubChannel;

  private final Thread _thread;

  /**
   * Creates and initializes an instance of a Session object.
   * @param options the options describing the Session instance.
   */
  public Session(SessionOptions options) {
    _options = options;

    _thread = new Thread(this);
    _thread.setName("Kernel Session Thread");
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
    _controlChannel = createChannel(MessageChannel.Control, ZMQ.ROUTER,
                                    _options.getControlPort());
    _shellChannel = createChannel(MessageChannel.Shell, ZMQ.ROUTER, _options.getShellPort());
    _stdinChannel = createChannel(MessageChannel.Input, ZMQ.ROUTER, _options.getStdinPort());
    _ioPubChannel = createChannel(MessageChannel.Output, ZMQ.PUB, _options.getIOPubPort());

    // Start the session thread, which will start reading and processing messages.
    _thread.start();

    // This runs the heart beat socket listening/echo'ing right on this thread.
    try {
      Thread.currentThread().setName("Main Thread");
      ZMQ.proxy(_heartbeatChannel.getSocket(), _heartbeatChannel.getSocket(), null);
    }
    catch (Exception e) {
      // TODO: Logging
    }
  }

  /**
   * Stops the session.
   */
  public void stop() {
    _thread.interrupt();

    _ioPubChannel.close();
    _stdinChannel.close();
    _shellChannel.close();
    _controlChannel.close();
    _heartbeatChannel.close();

    _context.term();
  }

  private void processChannel(Channel channel) {
    Message message = channel.receiveMessage();
    if (message == null) {
      return;
    }

    System.out.println("[" + channel.getChannelType() + "]: " + message.getType());
    MessageHandler handler = message.getHandler();
    if (handler == null) {
      // TODO: Logging
      return;
    }

    handler.handleMessage(message, channel.getChannelType(), this);
  }

  /**
   * {@link Runnable}
   */
  @Override
  public void run() {
    // Setup a poller, that will poll for messages on multiple sockets, and
    // indicate which one has incoming data that should be processed.

    Poller poller = new ZMQ.Poller(2);
    poller.register(_controlChannel.getSocket(), Poller.POLLIN);
    poller.register(_shellChannel.getSocket(), Poller.POLLIN);

    while (!Thread.currentThread().isInterrupted()) {
      try {
        // Poll indefinitely, until one of the channels has something to process.
        poller.poll();

        if (poller.pollin(0)) {
          processChannel(_controlChannel);
        }

        if (poller.pollin(1)) {
          processChannel(_shellChannel);
        }
      }
      catch (Exception e) {
        // TODO: Logging
      }
    }
  }

  /**
   * {@link MessageServices}
   */
  @Override
  public void sendMessage(Message message, MessageChannel target) {
    Channel channel = null;
    switch (target) {
      case Control:
        channel = _controlChannel;
        break;
      case Shell:
        channel = _shellChannel;
        break;
      case Input:
        channel = _stdinChannel;
        break;
      case Output:
        channel = _ioPubChannel;
        break;
      default:
        break;
    }

    if (channel != null) {
      channel.sendMessage(message);
    }
  }
}
