// ShellPluging.java
//

package ijava.extensibility;

/**
 * Implemented by ijava shell plugins.
 */
public interface ShellPlugin {

  /**
   * Initializes the plugin and associates it with the specified shell.
   * @param shell the ijava shell instance that the plugin is associated with.
   */
  public void initialize(Shell shell);
}
