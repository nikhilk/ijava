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

  private final Thread _shellThread;

  /**
   * Creates and initializes an instance of a Session object.
   * @param options the options describing the Session instance.
   */
  public Session(SessionOptions options) {
    _options = options;

    _shellThread = new Thread(new ShellChannelHandler());
    _shellThread.setName("Kernel Shell Channel Handler");
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
    _shellThread.start();

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
    _shellThread.interrupt();

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

    MessageHandler handler = message.getHandler();
    if (handler == null) {
      // TODO: Logging
      return;
    }

    handler.handleMessage(message, channel.getChannelType(), this);
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


  private final class ShellChannelHandler implements Runnable {

    /**
     * {@link Runnable}
     */
    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          processChannel(_shellChannel);
        }
        catch (Exception e) {
          // TODO: Logging
        }
      }
    }
  }
}
