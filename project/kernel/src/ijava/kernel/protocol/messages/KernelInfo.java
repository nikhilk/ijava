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

    @SuppressWarnings("unchecked")
    public ResponseMessage(JSONObject parentHeader) {
      super(Message.KernelInfoResponse, parentHeader);

      JSONArray protocolVersion = new JSONArray();
      protocolVersion.add(new Integer(4));
      protocolVersion.add(new Integer(1));

      JSONArray languageVersion = new JSONArray();
      languageVersion.add(new Integer(1));
      languageVersion.add(new Integer(7));

      JSONObject content = getContent();
      content.put("language", "java");
      content.put("language_version", languageVersion);
      content.put("protocol_version", protocolVersion);
    }
  }

  public static final class Handler implements MessageHandler {

    @Override
    public void handleMessage(Message message, MessageChannel source, MessageServices services) {
      ResponseMessage responseMessage = new ResponseMessage(message.getHeader());
      services.sendMessage(responseMessage, source);
    }
  }
}
