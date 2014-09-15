// Session.java
//

package ijava.kernel;

import org.zeromq.*;
import org.zeromq.ZMQ.*;

/**
 * Represents a running Kernel instance.
 */
public final class Session {

  private final static int ZMQ_IO_THREADS = 1;

  private final SessionOptions _options;

  private Context _context;
  private Socket _heartbeatSocket;
  private Socket _controlSocket;
  private Socket _shellSocket;
  private Socket _stdinSocket;
  private Socket _ioPubSocket;

  /**
   * Creates and initializes an instance of a Session object.
   * @param options the options describing the Session instance.
   */
  public Session(SessionOptions options) {
    _options = options;
  }

  private Socket createSocket(int type, int port) {
    Socket socket = _context.socket(type);

    String address = String.format("%s://%s:%d", _options.getTransport(), _options.getIP(), port);
    socket.bind(address);

    return socket;
  }

  /**
   * Starts the session.
   */
  public void start() {
    _context = ZMQ.context(Session.ZMQ_IO_THREADS);

    _heartbeatSocket = createSocket(ZMQ.REP, _options.getHeartbeatPort());
    _controlSocket = createSocket(ZMQ.ROUTER, _options.getControlPort());
    _shellSocket = createSocket(ZMQ.ROUTER, _options.getShellPort());
    _stdinSocket = createSocket(ZMQ.ROUTER, _options.getStdinPort());
    _ioPubSocket = createSocket(ZMQ.PUB, _options.getIOPubPort());

    ZMQ.proxy(_heartbeatSocket, _heartbeatSocket, null);
  }

  /**
   * Stops the session.
   */
  public void stop() {
    _ioPubSocket.close();
    _stdinSocket.close();
    _shellSocket.close();
    _controlSocket.close();
    _heartbeatSocket.close();

    _context.term();
  }
}
