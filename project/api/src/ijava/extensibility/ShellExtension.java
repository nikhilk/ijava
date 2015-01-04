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
   * @return an optional object to indicate the extension was initialized.
   */
  public Object initialize(Shell shell);
}
