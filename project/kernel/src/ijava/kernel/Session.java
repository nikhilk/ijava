// Session.java
//

package ijava.kernel;

import java.util.*;
import org.zeromq.*;
import org.zeromq.ZMQ.*;

import ijava.*;
import ijava.kernel.protocol.*;

/**
 * Represents a running Kernel instance.
 */
public final class Session implements MessageServices {

  private final static int ZMQ_IO_THREADS = 1;

  private final SessionOptions _options;
  private final Evaluator _evaluator;

  private final Context _context;
  private final Socket _controlSocket;
  private final Socket _shellSocket;
  private final Socket _ioPubSocket;

  private final SessionWorker _worker;

  private final Queue<Message> _publishQueue;

  private Boolean _stopped;


  /**
   * Creates and initializes an instance of a Session object.
   * @param options the options describing the Session instance.
   * @param runtimeFunction the function that processes tasks within the session.
   */
  public Session(SessionOptions options, Evaluator evaluator) {
    _options = options;
    _evaluator = evaluator;

    _context = ZMQ.context(Session.ZMQ_IO_THREADS);
    _controlSocket = createSocket(ZMQ.ROUTER, options.getControlPort());
    _shellSocket = createSocket(ZMQ.ROUTER, options.getShellPort());
    _ioPubSocket = createSocket(ZMQ.PUB, options.getIOPubPort());

    _worker = new SessionWorker(this);

    _publishQueue = new LinkedList<Message>();

    _stopped = true;
  }

  /**
   * Gets the evaluator associated with the session to evaluate inputs.
   * @return the current evaluation function.
   */
  public Evaluator getEvaluator() {
    return _evaluator;
  }

  Socket createSocket(int socketType, int port) {
    Socket socket = _context.socket(socketType);

    String address = String.format("%s://%s:%d", _options.getTransport(), _options.getIP(), port);
    socket.bind(address);

    return socket;
  }

  private void processIncomingMessage(Socket socket, MessageChannel channel) {
    Message message = MessageIO.readMessage(socket);
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
      message.associateChannel(channel);
      handler.handleMessage(message, Session.this);
    }
    catch (Exception e) {
      // TODO: Logging
    }
  }

  private void processOutgoingMessage(Message message) {
    Socket socket = null;
    switch (message.getChannel()) {
      case Control:
        socket = _controlSocket;
        break;
      case Shell:
        socket = _shellSocket;
        break;
      case Output:
        socket = _ioPubSocket;
        break;
      default:
        socket = null;
        break;
    }

    if (socket != null) {
      MessageIO.writeMessage(socket, message);
    }
  }

  /**
   * Starts the session.
   */
  public void start() {
    _stopped = false;

    // Start the worker to process tasks submitted into the session.
    _worker.start();

    // Start a thread to implement the kernel heartbeat.
    SessionHeartbeat.start(this, _options);

    // This thread will handle incoming socket messages and send out-going socket messages.
    // In other words, all socket processing occurs in the thread that the sockets were
    // created on.

    ZMQ.Poller poller = new ZMQ.Poller(2);
    poller.register(_controlSocket, ZMQ.Poller.POLLIN);
    poller.register(_shellSocket, ZMQ.Poller.POLLIN);

    while (!_stopped) {
      poller.poll(1000);

      if (poller.pollin(0)) {
        processIncomingMessage(_controlSocket, MessageChannel.Control);
      }
      if (poller.pollin(1)) {
        processIncomingMessage(_shellSocket, MessageChannel.Shell);
      }

      if (_publishQueue.size() != 0) {
        synchronized (_publishQueue) {
          while (!_publishQueue.isEmpty()) {
            Message message = _publishQueue.poll();
            processOutgoingMessage(message);
          }
        }
      }
    }

    _worker.stop();

    _controlSocket.close();
    _shellSocket.close();
    _ioPubSocket.close();
  }

  /**
   * Stops the session.
   */
  public void stop() {
    _stopped = true;

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
  public void processTask(String content, Message message) {
    SessionTask task = new SessionTask(content, message);
    _worker.addTask(task);
  }

  /**
   * {@link MessageServices}
   */
  @Override
  public void sendMessage(Message message) {
    synchronized (_publishQueue) {
      _publishQueue.add(message);
    }
  }
}
