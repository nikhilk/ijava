// Image.java
//

package ijava.data;

import java.net.*;
import java.util.*;
import sun.misc.*;

/**
 * Represents image data, either as raw image bytes, or image reference by URL.
 */
public final class Image {

  private final URI _uri;
  private final byte[] _data;
  private final String _mimeType;

  private String _alternateText;
  private String _width;
  private String _height;

  /**
   * Initializes an instance of an Image.
   * @param url the URL of the image to reference.
   * @throws URISyntaxException if the URL is an invalid URI.
   */
  public Image(String url) throws URISyntaxException {
    _uri = new URI(url);
    _data = null;
    _mimeType = null;
  }

  /**
   * Initializes an instance of an Image.
   * @param uri the URL of the image to reference.
   */
  public Image(URI uri) {
    _uri = uri;
    _data = null;
    _mimeType = null;
  }

  /**
   * Initializes an instance of an Image.
   * @param data the raw image bytes.
   * @param mimeType the type of image represented by the bytes.
   */
  public Image(byte[] data, String mimeType) {
    _uri = null;
    _data = data;
    _mimeType = mimeType;
  }

  /**
   * Adds alternate textual representation of the image.
   * @param text the alternate text.
   * @return the modified Image object.
   */
  public Image addAlternateText(String text) {
    _alternateText = text;
    return this;
  }

  /**
   * Adds dimensions for the image. Only applicable for image references.
   * @param width the width of the image (as specified in HTML). null if it is unspecified.
   * @param width the height of the image (as specified in HTML). null if it is unspecified.
   * @return the modified Image object.
   */
  public Image addDimensions(String width, String height) {
    _width = width;
    _height = height;
    return this;
  }

  /**
   * Generates a mime representation of this object.
   * @return a text/html representation of this object.
   */
  @SuppressWarnings("restriction")
  public Map<String, String> toMimeRepresentation() {
    HashMap<String, String> representations = new HashMap<String, String>();
    if (_alternateText != null) {
      representations.put("text/plain", _alternateText);
    }

    if (_uri != null) {
      StringBuilder sb = new StringBuilder();

      sb.append("<img src=\"");
      sb.append(_uri);
      sb.append("\"");

      if (_alternateText != null) {
        sb.append(" alt=\"");
        sb.append(_alternateText.replace("&", "&amp;").replace("\"", "&quot;"));
        sb.append("\"");
      }

      if (_width != null) {
        sb.append(" width=\"");
        sb.append(_width);
        sb.append("\"");
      }
      if (_height != null) {
        sb.append(" height=\"");
        sb.append(_height);
        sb.append("\"");
      }

      sb.append(" />");

      representations.put("text/html", sb.toString());
    }
    else {
      BASE64Encoder encoder = new BASE64Encoder();
      representations.put(_mimeType, encoder.encode(_data));
    }

    return representations;
  }
}

