// SessionHeartbeat.java
//

package ijava.kernel;

import org.zeromq.*;
import org.zeromq.ZMQ.*;

/**
 * Implements a heart beat using ZMQ.
 */
public final class SessionHeartbeat implements Runnable {

  private final Session _session;
  private final SessionOptions _options;

  /**
   * Creates a Heartbeat instance for the specified session.
   * @param session the session to create a heart beat for.
   * @param options the options used to configure the session.
   */
  private SessionHeartbeat(Session session, SessionOptions options) {
    _session = session;
    _options = options;
  }

  /**
   * Starts a Heartbeat instance for the specified session. This runs while the process-wide
   * ZMQ context has not been terminated.
   * @param session the session to create a heart beat for.
   * @param options the options used to configure the session.
   */
  public static void start(Session session, SessionOptions options) {
    Thread heartbeatThread = new Thread(new SessionHeartbeat(session, options));

    heartbeatThread.setName("Heartbeat");
    heartbeatThread.setDaemon(true);

    heartbeatThread.start();
  }

  /**
   * {@link Runnable}
   */
  @Override
  public void run() {
    Socket socket = _session.createSocket(ZMQ.REP, _options.getHeartbeatPort());
    ZMQ.proxy(socket, socket, /* capture */ null);
  }
}
