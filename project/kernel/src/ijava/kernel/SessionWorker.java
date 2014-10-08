// SessionWorker.java
//

package ijava.kernel;

import java.io.*;
import java.util.*;

import ijava.kernel.protocol.*;
import ijava.kernel.protocol.messages.*;

/**
 * Processes tasks within the kernel session.
 */
public final class SessionWorker implements Runnable {

  private final static int SLEEP_INTERVAL = 500;

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

  private int processTask(SessionTask task, int counter) {
    Message parentMessage = task.getMessage();
    String content = task.getContent();

    if (content.isEmpty()) {
      Execute.ResponseMessage responseMessage =
          new Execute.SuccessResponseMessage(parentMessage.getIdentity(),
                                             parentMessage.getHeader(),
                                             counter);
      _session.sendMessage(responseMessage.associateChannel(parentMessage.getChannel()));

      // Nothing to execute, so return the counter without incrementing.
      return counter;
    }

    PrintStream stdout = System.out;
    PrintStream stderr = System.err;
    InputStream stdin = System.in;

    PrintStream capturedStdout =
        new CapturedPrintStream(StreamMessage.STDOUT, parentMessage, stdout);
    PrintStream capturedStderr =
        new CapturedPrintStream(StreamMessage.STDERR, parentMessage, stderr);
    InputStream disabledStdin = new DisabledInputStream();

    Exception error = null;
    Object result = null;
    try {
      System.setOut(capturedStdout);
      System.setErr(capturedStderr);
      System.setIn(disabledStdin);

      result = _session.getEvaluator().evaluate(task.getContent());
    }
    catch (Exception e) {
      e.printStackTrace();
      error = e;
    }
    finally {
      // Flush the captured streams. This will send out any pending stream data to the client.
      capturedStdout.flush();
      capturedStderr.flush();

      System.setOut(stdout);
      System.setErr(stderr);
      System.setIn(stdin);
    }

    // Send a message to display the result, if there was any.
    Map<String, String> data = DataMessage.createData(result);
    if (data != null) {
      DataMessage dataMessage =
          new DataMessage(parentMessage.getIdentity(), parentMessage.getHeader(), data);
      _session.sendMessage(dataMessage.associateChannel(MessageChannel.Output));
    }

    // Send the success/failed result as a result of performing the task.
    Execute.ResponseMessage responseMessage;
    if (error == null) {
      responseMessage =
          new Execute.SuccessResponseMessage(parentMessage.getIdentity(), parentMessage.getHeader(),
                                             counter);
    }
    else {
      responseMessage =
          new Execute.ErrorResponseMessage(parentMessage.getIdentity(), parentMessage.getHeader(),
                                           counter,
                                           error);
    }
    _session.sendMessage(responseMessage.associateChannel(parentMessage.getChannel()));

    return counter + 1;
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
    boolean busy = false;
    int counter = 1;

    while (!Thread.currentThread().isInterrupted()) {
      SessionTask task = null;
      synchronized (_tasks) {
        task = _tasks.poll();
      }

      if (task != null) {
        if (!busy) {
          // Transitioning from idle to busy
          busy = true;
          _session.sendMessage(StatusMessage.createBusyStatus());
        }

        counter = processTask(task, counter);
      }
      else {
        if (busy) {
          // Transitioning from busy to idle
          busy = false;
          _session.sendMessage(StatusMessage.createIdleStatus());
        }
      }

      // TODO: Synchronized access to check for empty?
      if (_tasks.isEmpty()) {
        try {
          Thread.sleep(SessionWorker.SLEEP_INTERVAL);
        }
        catch (InterruptedException e) {
        }
      }
    }
  }


  /**
   * Implements a PrintStream that has been captured, allowing output to be redirected
   * to the client of the current session.
   */
  private final class CapturedPrintStream extends PrintStream {

    private final static int MAX_BUFFER_SIZE = 240;

    private final String _name;
    private final Message _parentMessage;

    private StringBuilder _buffer;

    /**
     * Initializes a CapturedPrintStream instance with the stream name.
     * @param name the name of the stream.
     * @param parentMessage the associated message being processed.
     * @param out the underlying output stream.
     */
    public CapturedPrintStream(String name, Message parentMessage, OutputStream out) {
      super(out);
      _name = name;
      _parentMessage = parentMessage;
    }

    @Override
    public void flush() {
      if (_buffer != null) {
        String text = _buffer.toString();
        _buffer = null;

        StreamMessage message = new StreamMessage(_parentMessage.getIdentity(),
                                                  _parentMessage.getHeader(),
                                                  _name, text);
        _session.sendMessage(message.associateChannel(MessageChannel.Output));
      }
    }

    @Override
    public void print(String s) {
      if (_buffer == null) {
        _buffer = new StringBuilder();
      }

      _buffer.append(s);
      if (_buffer.length() >= CapturedPrintStream.MAX_BUFFER_SIZE) {
        flush();
      }
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
      String error = "Reading from System.in is not supported. " +
          "All input should be specified at the time of execution.";
      throw new UnsupportedOperationException(error);
    }
  }
}
