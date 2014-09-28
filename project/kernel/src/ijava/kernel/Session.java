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
  private Channel _controlChannel;
  private Channel _shellChannel;
  private Channel _stdinChannel;
  private Channel _ioPubChannel;
  private Socket _heartbeatSocket;

  private Thread _shellThread;
  private Thread _controlThread;
  private Thread _heartbeatThread;

  /**
   * Creates and initializes an instance of a Session object.
   * @param options the options describing the Session instance.
   */
  public Session(SessionOptions options) {
    _options = options;
  }

  private Channel createChannel(MessageChannel channelType, int socketType, int port) {
    Socket socket = createSocket(socketType, port);
    return new Channel(channelType, createSocket(socketType, port));
  }

  private Socket createSocket(int socketType, int port) {
    Socket socket = _context.socket(socketType);

    String address = String.format("%s://%s:%d", _options.getTransport(), _options.getIP(), port);
    socket.bind(address);

    return socket;
  }

  /**
   * Starts the session.
   */
  public void start() {
    _context = ZMQ.context(Session.ZMQ_IO_THREADS);

    _controlChannel = createChannel(MessageChannel.Control, ZMQ.ROUTER,
                                    _options.getControlPort());
    _shellChannel = createChannel(MessageChannel.Shell, ZMQ.ROUTER, _options.getShellPort());
    _stdinChannel = createChannel(MessageChannel.Input, ZMQ.ROUTER, _options.getStdinPort());
    _ioPubChannel = createChannel(MessageChannel.Output, ZMQ.PUB, _options.getIOPubPort());

    _heartbeatSocket = createSocket(ZMQ.REP, _options.getHeartbeatPort());

    // Start the channel threads, which will start reading and processing messages.
    _shellThread = new Thread(new ChannelHandler(_shellChannel));
    _shellThread.setName("Shell Thread");
    _shellThread.start();

    _controlThread = new Thread(new ChannelHandler(_controlChannel));
    _shellThread.setName("Control Thread");
    _controlThread.start();

    // Start the heartbeat thread, which will simply echo what it receives.
    _heartbeatThread = new Thread(new Heartbeat());
    _heartbeatThread.setName("Heartbeat");
    _heartbeatThread.start();

    try {
      // This runs the heart beat socket listening/echo'ing right on this thread.
      // The process should exit when this is the only thread left, so mark it as a daemon.

      Thread.currentThread().setName("Main Thread");
      Thread.currentThread().setDaemon(true);
      _heartbeatThread.join();
    }
    catch (Exception e) {
      // TODO: Logging
    }
  }

  /**
   * Stops the session.
   */
  public void stop() {
    _ioPubChannel.close();
    _stdinChannel.close();
    _shellChannel.close();
    _controlChannel.close();
    _heartbeatSocket.close();

    if (_shellThread != null) {
      _shellThread.interrupt();
      _shellThread = null;
    }

    if (_controlThread != null) {
      _controlThread.interrupt();
      _controlThread = null;
    }

    if (_heartbeatThread != null) {
      _heartbeatThread.interrupt();
      _heartbeatThread = null;
    }

    _context.close();
    _context.term();
  }

  /**
   * {@link MessageServices}
   */
  @Override
  public void endSession() {
    System.exit(0);
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


  private final class ChannelHandler implements Runnable {

    private final Channel _channel;

    /**
     * Initializes a ChannelHandler with the channel it should process.
     * @param channel the channel to process.
     */
    public ChannelHandler(Channel channel) {
      _channel = channel;
    }

    /**
     * {@link Runnable}
     */
    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        Message message = _channel.receiveMessage();
        if (message == null) {
          return;
        }

        MessageHandler handler = message.getHandler();
        if (handler == null) {
          System.out.println("Unhandled message: " + message.getType());
          // TODO: Logging
          return;
        }

        try {
          handler.handleMessage(message, _channel.getChannelType(), Session.this);
        }
        catch (Exception e) {
          // TODO: Logging
        }
      }
    }
  }


  private final class Heartbeat implements Runnable {

    /**
     * {@link Runnable}
     */
    @Override
    public void run() {
      ZMQ.proxy(_heartbeatSocket, _heartbeatSocket, null);
    }
  }
}
