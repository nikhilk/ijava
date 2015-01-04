// SnippetCodeMember.java
//

package ijava.shell.compiler;

/**
 * Represents a field or method class member declared in a snippet.
 */
public final class SnippetCodeMember {

  private final boolean _field;
  private final String _name;
  private final String _type;
  private final String _code;

  /**
   * Initializes an instance of a SnippetCodeMember.
   * @param field whether the member is a field.
   * @param name the name of the member.
   * @param type the type declaration of a field.
   * @param code the code representation of a method.
   */
  private SnippetCodeMember(boolean field, String name, String type, String code) {
    _field = field;
    _name = name;
    _type = type;
    _code = code;
  }

  /**
   * Gets the code block representing a member method.
   * @return the method declaration and body.
   */
  public String getCode() {
    return _code;
  }

  /**
   * Gets the name of the member field or method.
   * @return the identifier of the member.
   */
  public String getName() {
    return _name;
  }

  /**
   * Gets the type declaration of the member field.
   * @return the type declaration.
   */
  public String getType() {
    return _type;
  }

  /**
   * Whether this member is a field.
   * @return true if this member is a field; false otherwise.
   */
  public boolean isField() {
    return _field;
  }

  /**
   * Whether this member is a method.
   * @return true if this member is a method; false otherwise.
   */
  public boolean isMethod() {
    return !_field;
  }

  /**
   * Creates a field member.
   * @param name the name of the field.
   * @param type the type declaration of the field.
   * @return a SnippetCodeMember representing the field.
   */
  public static SnippetCodeMember createField(String name, String type) {
    return new SnippetCodeMember(/* field */ true, name, type, /* code */ null);
  }

  /**
   * Creates a method member.
   * @param name the name of the method.
   * @param code the code representing the method.
   * @return a SnippetCodeMember representing the method.
   */
  public static SnippetCodeMember createMethod(String name, String code) {
    return new SnippetCodeMember(/* field */ false, name, /* type */ null, code);
  }
}
