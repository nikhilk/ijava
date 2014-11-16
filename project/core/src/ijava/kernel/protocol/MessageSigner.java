// MessageSigner.java
//

package ijava.kernel.protocol;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Provides support for message signing to allow validating messages.
 */
public class MessageSigner {

  private static final String DEFAULT_ALGORITHM = "hmacsha256";

  protected MessageSigner() {
  }

  /**
   * Creates a MessageSigner implementation to use given a key and algorithm.
   * @param key the key to use in signing.
   * @param algorithm the algorithm to use in signing.
   * @return a MessageSigner that can be used to sign messages.
   */
  public static MessageSigner create(String key, String algorithm) {
    if ((key == null) || key.isEmpty()) {
      return new MessageSigner();
    }

    if ((algorithm != null) && !algorithm.isEmpty()) {
      algorithm = algorithm.replace("-", "");
    }
    else {
      algorithm = MessageSigner.DEFAULT_ALGORITHM;
    }

    try {
      Mac mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(key.getBytes(), algorithm));

      return new HashMessageSigner(mac);
    }
    catch (Exception e) {
      // TODO: Logging
      throw new IllegalArgumentException("Invalid key or algorithm.");
    }
  }

  /**
   * Returns a signature of the specified arguments by computing its hash.
   * @param args the set of strings to sign.
   * @return a hex encoded representation of the signature.
   */
  public String signature(String... args) {
    return "";
  }

  /**
   * Validates incoming data against the accompanying signature.
   * @param signature the specified signature accompanying the data.
   * @param args the incoming data to be validated.
   * @return true if the data is valid, false otherwise.
   */
  public boolean validate(String signature, String... args) {
    String computedSignature = signature(args);
    return signature.toLowerCase().equals(computedSignature);
  }


  /**
   * Implements a MessageSigner using hashing functionality.
   */
  private static final class HashMessageSigner extends MessageSigner {

    protected final Mac _mac;

    public HashMessageSigner(Mac mac) {
      _mac = mac;
    }

    @Override
    public String signature(String... args) {
      for (String s : args) {
        if (s != null) {
          _mac.update(s.getBytes());
        }
      }

      StringBuilder sb = new StringBuilder();
      for (byte b : _mac.doFinal()) {
        sb.append(String.format("%02X", b));
      }

      return sb.toString().toLowerCase();
    }
  }
}
