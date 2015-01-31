// CodeCommands.java
//

package ijava.shell;

import java.util.*;
import com.beust.jcommander.*;
import ijava.extensibility.*;

public final class CodeCommands {

  private CodeCommands() {
  }


  public static final class TupleCommand extends Command<TupleCommand.Options> {

    public TupleCommand(Shell shell) {
      super(shell, Options.class, /* singleLine */ false);
    }

    @Override
    public Object evaluate(Options options, long evaluationID, Map<String, Object> metadata)
        throws Exception {
      StringBuilder codeBuilder = new StringBuilder();
      codeBuilder.append("public final class ");
      codeBuilder.append(options.name);
      codeBuilder.append(" {\n");

      StringBuilder paramsBuilder = new StringBuilder();
      StringBuilder ctorBuilder = new StringBuilder();

      StringBuilder toStringBuilder = new StringBuilder();
      toStringBuilder.append("    StringBuilder __sb = new StringBuilder();\n");
      toStringBuilder.append("    __sb.append('[');\n");

      // TODO: Consider using a YAML parser instead

      Scanner s = new Scanner(options.getContent());
      try {
        boolean firstField = true;
        int lineNumber = 0;
        while (s.hasNextLine()) {
          lineNumber++;

          String line = s.nextLine().trim();
          if (line.isEmpty() || line.startsWith("//")) {
            continue;
          }

          if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
          }

          String fieldName;
          String fieldType;

          String[] fieldParts = line.split(":");
          if (fieldParts.length == 1) {
            fieldName = line;
            fieldType = options.defaultType;
          }
          else if (fieldParts.length == 2) {
            fieldName = fieldParts[0].trim();
            fieldType = fieldParts[1].trim();
          }
          else {
            throw new EvaluationError("[" + lineNumber + "] Invalid 'name: type' syntax.");
          }

          codeBuilder.append("  public ");
          if (options.immutable) {
            codeBuilder.append("final ");
          }
          codeBuilder.append(fieldType).append(" ");
          codeBuilder.append(fieldName).append(";\n");

          if (!firstField) {
            paramsBuilder.append(", ");
          }
          paramsBuilder.append(fieldType).append(" ");
          paramsBuilder.append(fieldName);

          ctorBuilder.append("    this.").append(fieldName);
          ctorBuilder.append(" = ").append(fieldName).append(";\n");

          if (!firstField) {
            toStringBuilder.append("    __sb.append(';');\n");
          }
          toStringBuilder.append("    __sb.append(\" " + fieldName + "\");\n");
          toStringBuilder.append("    __sb.append(':');\n");
          toStringBuilder.append("    __sb.append(" + fieldName + ");\n");

          firstField = false;
        }
      }
      finally {
        s.close();
      }

      toStringBuilder.append("    __sb.append(\" ]\");\n");
      toStringBuilder.append("    return __sb.toString();\n");

      if (!options.immutable) {
        codeBuilder.append("  public ").append(options.name).append("() { }\n");
      }
      codeBuilder.append("  public ").append(options.name).append("(");
      codeBuilder.append(paramsBuilder.toString()).append(") {\n");
      codeBuilder.append(ctorBuilder.toString());
      codeBuilder.append("  }\n");
      codeBuilder.append("  @Override public String toString() {\n");
      codeBuilder.append(toStringBuilder.toString());
      codeBuilder.append("  }\n");

      codeBuilder.append("}\n");

      String code = codeBuilder.toString();
      return getShell().evaluate(code, evaluationID, metadata);
    }


    public static final class Options extends CommandOptions {

      @Parameter(names = "--name", description = "The name of the tuple", required = true)
      public String name;

      @Parameter(names = "--defaultType", description = "The default type of members")
      public String defaultType = "String";

      @Parameter(names = "--immutable", description = "Whether to generate an immutable tuple.")
      public boolean immutable = false;
    }
  }
}
