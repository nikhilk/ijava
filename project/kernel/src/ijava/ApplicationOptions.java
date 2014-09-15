// ApplicationOptions.java
//

package ijava;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import org.json.simple.*;
import org.json.simple.parser.*;

final class ApplicationOptions {

  private String _ip;
  private String _transport;

  private String _signatureKey;
  private String _signatureScheme;

  private int _heartbeatPort;
  private int _controlPort;
  private int _shellPort;
  private int _ioPubPort;
  private int _stdinPort;

  private ApplicationOptions() {
  }

  public int getControlPort() {
    return _controlPort;
  }

  public int getHeartbeatPort() {
    return _heartbeatPort;
  }

  public String getIP() {
    return _ip;
  }

  public int getIOPubPort() {
    return _ioPubPort;
  }

  public int getShellPort() {
    return _shellPort;
  }

  public String getSignatureKey() {
    return _signatureKey;
  }

  public String getSignatureScheme() {
    return _signatureScheme;
  }

  public int getStdinPort() {
    return _stdinPort;
  }

  public String getTransport() {
    return _transport;
  }

  public static ApplicationOptions load(String optionsPath) {
    ApplicationOptions options = null;

    String json = ApplicationOptions.readOptions(optionsPath);
    if (json.length() != 0) {
      JSONParser parser = new JSONParser();
      try {
        JSONObject optionsObject = (JSONObject)parser.parse(json);

        options = new ApplicationOptions();
        options._ip = (String)optionsObject.get("ip");
        options._transport = (String)optionsObject.get("transport");
        options._signatureKey = (String)optionsObject.get("key");
        options._signatureScheme = (String)optionsObject.get("signature_scheme");
        options._heartbeatPort = (int)(long)optionsObject.get("hb_port");
        options._controlPort = (int)(long)optionsObject.get("control_port");
        options._shellPort = (int)(long)optionsObject.get("shell_port");
        options._ioPubPort = (int)(long)optionsObject.get("iopub_port");
        options._stdinPort = (int)(long)optionsObject.get("stdin_port");
      }
      catch (Exception e) {
        // TODO: Logging
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
      // TODO: Logging
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
