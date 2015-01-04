// ShellExtension.java
//

package ijava.extensibility;

/**
 * Implemented by ijava shell extensions.
 */
public interface ShellExtension {

  /**
   * Initializes the extension and associates it with the specified shell.
   * @param shell the ijava shell instance that the extension is associated with.
   */
  public void initialize(Shell shell);
}
