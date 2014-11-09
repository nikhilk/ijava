// JavaExtensions.java
//

package ijava.shell;

import java.net.*;
import java.util.*;
import com.fasterxml.jackson.jr.ob.*;

/**
 * Standard Java-language related extensions.
 */
public final class JavaExtensions {

  private JavaExtensions() {
  }

  /**
   * Handles %dependency invocations to add dependencies to modules to subsequent compilations.
   */
  public static final class DependencyExtension implements Extension {

    @Override
    public Object evaluate(JavaShell shell, String declaration, String data) throws Exception {
      if (declaration.startsWith("'") || declaration.startsWith("\"")) {
        declaration = declaration.substring(1, declaration.length() - 1);
      }

      shell.addDependency(URI.create(declaration));
      return null;
    }
  }

  /**
   * Handles %jars invocations to list the current set of jar dependencies.
   */
  public static final class JarsExtension implements Extension {

    @Override
    public Object evaluate(JavaShell shell, String declaration, String data) throws Exception {
      String[] jars = shell.getJars();
      Arrays.sort(jars);

      StringBuilder sb = new StringBuilder();
      for (String s: jars) {
        sb.append(s);
        sb.append("\n");
      }

      return sb.toString();
    }
  }

  /**
   * Handles %imports invocations to list the current set of imports.
   */
  public static final class ImportsExtension implements Extension {

    @Override
    public Object evaluate(JavaShell shell, String declaration, String data) throws Exception {
      String[] imports = shell.getImports().split(";");
      Arrays.sort(imports);

      StringBuilder sb = new StringBuilder();
      for (String s: imports) {
        sb.append(s);
        sb.append(";\n");
      }

      return sb.toString();
    }
  }

  /**
   * Handles %%text invocations to create a String instance.
   */
  public static final class TextExtension implements Extension {

    @Override
    public Object evaluate(JavaShell shell, String declaration, String data) throws Exception {
      if (declaration.length() != 0) {
        shell.getState().declareField(declaration, "String");
        shell.getState().setValue(declaration, data);

        return null;
      }
      else {
        return data;
      }
    }
  }

  /**
   * Handles %%json invocations to parse a JSON formatted data.
   */
  public static final class JsonExtension implements Extension {

    @Override
    public Object evaluate(JavaShell shell, String declaration, String data) throws Exception {
      Object value = null;
      String name = null;

      data = data.trim();
      if (data.startsWith("{")) {
        value = JSON.std.mapFrom(data);
        name = "Map<String, Object>";
      }
      else if (data.startsWith("[")) {
        value = JSON.std.listFrom(data);
        name = "List<Object>";
      }
      else {
        throw new IllegalArgumentException("Invalid JSON. Must be either an object or an array.");
      }

      if (declaration.length() != 0) {
        shell.getState().declareField(declaration, name);
        shell.getState().setValue(declaration, value);

        return null;
      }
      else {
        return value;
      }
    }
  }
}
