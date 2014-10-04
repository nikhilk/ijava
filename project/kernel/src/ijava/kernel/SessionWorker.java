// SessionWorker.java
//

package ijava.kernel;

import java.util.*;

/**
 * Processes tasks within the kernel session.
 */
public final class SessionWorker implements Runnable {

  private final Queue<SessionTask> _tasks;
  private final Thread _thread;

  /**
   * Creates an instance of a SessionWorker.
   */
  public SessionWorker() {
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
    // TODO: Implement this
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
}
