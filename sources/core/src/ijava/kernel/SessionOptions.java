// ApplicationOptions.java
//

package ijava.kernel;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.jr.ob.*;

/**
 * Represents options that control the behavior of the current session.
 */
public final class SessionOptions {

  private String _ip;
  private String _transport;

  private String _signatureKey;
  private String _signatureScheme;

  private int _heartbeatPort;
  private int _controlPort;
  private int _shellPort;
  private int _ioPubPort;
  private int _stdinPort;

  private SessionOptions() {
  }

  /**
   * Gets the Control part.
   * @return The control port socket number.
   */
  public int getControlPort() {
    return _controlPort;
  }

  /**
   * Gets the Heartbeat part.
   * @return The control port socket number.
   */
  public int getHeartbeatPort() {
    return _heartbeatPort;
  }

  /**
   * Gets the IP of the domain being used for sockets addresses.
   * @return The IP address of the domain.
   */
  public String getIP() {
    return _ip;
  }

  /**
   * Gets the port of the domain being used for sockets addresses.
   * @return The port of the socket.
   */
  public int getIOPubPort() {
    return _ioPubPort;
  }

  /**
   * Gets the port of the domain being used for sockets addresses.
   * @return The port of the socket.
   */
  public int getShellPort() {
    return _shellPort;
  }

  /**
   * Gets the key being used for signing messages.
   * @return the key to use to verify the messages.
   */
  public String getSignatureKey() {
    return _signatureKey;
  }

  /**
   * Gets the type of signing to use for signing messages
   * @return the type of signing to use.
   */
  public String getSignatureScheme() {
    return _signatureScheme;
  }

  /**
   * Gets the port of the domain being used for sockets addresses.
   * @return The port of the socket.
   */
  public int getStdinPort() {
    return _stdinPort;
  }

  /**
   * The network transport to use to perform communication.
   * @return The transport model to use.
   */
  public String getTransport() {
    return _transport;
  }

  /**
   * Loads the specified options path to construct an Options instance.
   * @param optionsPath the path of the options file.
   * @return a parsed SessionOptions instance.
   */
  public static SessionOptions load(String optionsPath) {
    SessionOptions options = null;

    String json = SessionOptions.readOptions(optionsPath);
    if (json.length() != 0) {
      try {
        Map<String, Object> optionsMap = JSON.std.mapFrom(json);

        options = new SessionOptions();
        options._ip = (String)optionsMap.get("ip");
        options._transport = (String)optionsMap.get("transport");
        options._signatureKey = (String)optionsMap.get("key");
        options._signatureScheme = (String)optionsMap.get("signature_scheme");
        options._heartbeatPort = (int)optionsMap.get("hb_port");
        options._controlPort = (int)optionsMap.get("control_port");
        options._shellPort = (int)optionsMap.get("shell_port");
        options._ioPubPort = (int)optionsMap.get("iopub_port");
        options._stdinPort = (int)optionsMap.get("stdin_port");
      }
      catch (Exception e) {
        // NOTE: Can't log, as log has not been initialized before startup
        System.err.println("Unable to read session options. Content:");
        System.err.println(json);
      }
    }

    return options;
  }

  private static String readOptions(String optionsPath) {
    try {
      byte[] bytes = Files.readAllBytes(Paths.get(optionsPath));
      return new String(bytes, StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      // NOTE: Can't log, as log has not been initialized before startup
      System.err.println(String.format("Unable to load session options file at %s", optionsPath));
      return "";
    }
  }

  @Override
  public String toString() {
    return String.format("%s:%s;\n[hb: %d] [ctrl: %d] [shell: %d] [pub: %d] [in: %d]",
                         _ip, _transport,
                         _heartbeatPort, _controlPort, _shellPort, _ioPubPort, _stdinPort);
  }
}
