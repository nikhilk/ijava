// ExtensionData.java
//

package ijava.shell;

/**
 * Represents the data for an extension invocation.
 */
public final class ExtensionData {

  private final String _name;
  private final String _declaration;
  private final String _content;

  /**
   * Initializes an instance of an ExtensionData.
   * @param name the name of the extension.
   * @param declaration the declaration for the extension.
   * @param content the content for the extension.
   */
  public ExtensionData(String name, String declaration, String content) {
    _name = name;
    _declaration = declaration;
    _content = content;
  }

  /**
   * Gets the content associated with the extension.
   * @return the content for the extension.
   */
  public String getContent() {
    return _content;
  }

  /**
   * Gets the declaration associated with the extension.
   * @return the declaration for the extension.
   */
  public String getDeclaration() {
    return _declaration;
  }

  /**
   * Gets the name of the extension to be invoked.
   * @return the name of the extension.
   */
  public String getName() {
    return _name;
  }
}
