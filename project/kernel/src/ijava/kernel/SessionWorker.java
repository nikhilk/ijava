// SessionWorker.java
//

package ijava.kernel;

import java.io.*;
import java.util.*;

import ijava.*;
import ijava.kernel.protocol.messages.*;

/**
 * Processes tasks within the kernel session.
 */
public final class SessionWorker implements Runnable {

  private final Session _session;

  private final Queue<SessionTask> _tasks;
  private final Thread _thread;

  /**
   * Creates an instance of a SessionWorker.
   * @param session the associated session that this worker is part of.
   */
  public SessionWorker(Session session) {
    _session = session;
    _tasks = new LinkedList<SessionTask>();

    _thread = new Thread(this);
    _thread.setName("Worker");
    _thread.setDaemon(true);
  }

  /**
   * Adds a task to the worker's queue.
   * @param task
   */
  public void addTask(SessionTask task) {
    synchronized (_tasks) {
      _tasks.add(task);
    }
  }

  private void processTask(SessionTask task) {
    Evaluator evaluator = _session.getEvaluator();

    PrintStream stdout = System.out;
    PrintStream stderr = System.err;
    InputStream stdin = System.in;

    PrintStream capturedStdout = new CapturedPrintStream(StreamMessage.STDOUT, stdout);
    PrintStream capturedStderr = new CapturedPrintStream(StreamMessage.STDERR, stderr);
    InputStream disabledStdin = new DisabledInputStream();

    try {
      System.setOut(capturedStdout);
      System.setErr(capturedStderr);
      System.setIn(disabledStdin);

      Object result = evaluator.evaluate(task.getContent());

      // TODO: Implement this fully
    }
    catch (Exception e) {
    }
    finally {
      capturedStdout.flush();
      capturedStderr.flush();

      System.setOut(stdout);
      System.setErr(stderr);
      System.setIn(stdin);
    }
  }

  /**
   * Starts the task processing.
   */
  public void start() {
    _thread.start();
  }

  /**
   * Stops the task processing.
   */
  public void stop() {
    _thread.interrupt();
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      if (!_tasks.isEmpty()) {
        SessionTask task = null;

        synchronized (_tasks) {
          task = _tasks.poll();
        }

        if (task != null) {
          processTask(task);
        }
      }
    }
  }


  /**
   * Implements a PrintStream that has been captured, allowing output to be redirected
   * to the client of the current session.
   */
  private final class CapturedPrintStream extends PrintStream {

    private final String _name;

    /**
     * Initializes a CapturedPrintStream instance with the stream name.
     * @param name the name of the stream.
     * @param out the underlying output stream.
     */
    public CapturedPrintStream(String name, OutputStream out) {
      super(out);
      _name = name;
    }

    @Override
    public void flush() {
      // TODO: Implement this
    }

    @Override
    public void print(String s) {
      // TODO: Implement this
    }
  }


  /**
   * Implements an InputStream that has been disabled, i.e. cannot be read from. Attempts
   * to read result in an exception.
   */
  private final class DisabledInputStream extends InputStream {

    /**
     * {@link InputStream}
     */
    @Override
    public int read() throws IOException {
      // TODO Auto-generated method stub
      return 0;
    }
  }
}
