// KernalInfo.java
//

package ijava.kernel.protocol.messages;

import org.json.simple.*;
import ijava.kernel.protocol.*;

public final class KernelInfo {

  private KernelInfo() {
  }

  public static final class RequestMessage extends Message {

    public RequestMessage(JSONObject header, JSONObject parentHeader, JSONObject metadata,
                          JSONObject content) {
      super(header, parentHeader, metadata, content);
    }
  }

  public static final class ResponseMessage extends Message {

    public ResponseMessage(JSONObject parentHeader) {
      super(Message.KernelInfoResponse, parentHeader);
    }
  }

  public static final class Handler implements MessageHandler {

    @Override
    public void handleMessage(Message message, MessageChannel source, MessageServices services) {
      // TODO: Implement this
    }
  }
}
