// MessageChannel.java
//

package ijava.kernel.protocol;

/**
 * Represents the type of a channel.
 */
public enum MessageChannel {

  /**
   * Represents the channel associated with the heart beat socket.
   */
  Heartbeat,

  /**
   * Represents the channels associated with the shell and control sockets.
   */
  Requests,

  /**
   * Represents the channel associated with the stdin socket.
   */
  Input,

  /**
   * Represents the channel associated with the iopub socket.
   */
  Output
}
