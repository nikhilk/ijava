// SnippetParser.java
//

package ijava.shell.compiler;

import java.util.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.core.dom.*;

/**
 * Provides the ability to inspect and infer metadata about a block of code text,
 * and produce a Snippet as a result.
 */
public final class SnippetParser {

  private final static String ERROR_MISSING_TYPE_DECLARATION =
      "Unable to parse. Failed to find a type declaration in top-level code snippet.";
  private final static String ERROR_COULD_NOT_PARSE =
      "Unable to parse the code block.";
  private final static String ERROR_INITIALIZER_NOT_SUPPORTED =
      "Class initializers are not supported outside of an explicitly declared class.";
  private final static String ERROR_ANNOTATIONTYPE_MEMBER_NOT_SUPPORTED =
      "Annotation type members are not supported outside of an explicitly declared class.";
  private final static String ERROR_ENUM_MEMBER_NOT_SUPPORTED =
      "Enum members are not supported outside of an explicitly declared class.";
  private final static String ERROR_NESTED_TYPES_NOT_SUPPORTED =
      "Nested types are not supported along-side other class member declarations.";
  private final static String ERROR_CTOR_NOT_SUPPORTED =
      "Constructors are not supported outside of an explicitly declared class.";
  private final static String ERROR_NOT_SUPPORTED =
      "Unsupported class member declaration.";

  /**
   * Parse the specified string of code into a Snippet.
   * @param code the text to be parsed.
   * @param id a numeric id to use to generate unique names.
   * @return the Snippet object and associated metadata.
   * @throws SnippetException if there is an error parsing the code.
   */
  public Snippet parse(String code, int id) throws SnippetException {
    List<String> errors = new ArrayList<String>();

    // First, attempt to parse the code as a complete java file, i.e. a compilation unit.
    String className = parseAsCompilationUnit(code, errors);
    if (className != null) {
      if (errors.size() != 0) {
        throw new SnippetException(errors.get(0));
      }

      return Snippet.compilationUnit(code, className);
    }

    String generatedClassName = "__Class" + id + "__";

    // If that didn't work, next attempt to parse the code as the body of a class, i.e. a set of
    // class members.
    List<SnippetCodeMember> classMembers = parseAsClassMembers(code, errors);
    if (classMembers != null) {
      if (errors.size() != 0) {
        throw new SnippetException(errors);
      }

      return Snippet.classMembers(code, generatedClassName, classMembers);
    }

    // Finally try parsing as a set of statements, which is a catch-all scenario.
    parseAsCodeBlock(code, errors);
    if (errors.size() == 0) {
      return Snippet.codeBlock(code, generatedClassName);
    }

    throw new SnippetException(errors);
  }

  /**
   * Parses a specified block of code into an equivalent AST. It attempts to parse the
   * code based on the specified snippet type.
   * @param code the code to parse.
   * @param attemptedCodeType the type of parsing to perform.
   * @return the resulting AST.
   */
  private ASTNode parseCode(String code, SnippetType attemptedCodeType) {
    // Create a parser that supports code written to Java 1.7.
    ASTParser parser = ASTParser.newParser(AST.JLS4);

    switch (attemptedCodeType) {
      case CompilationUnit:
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        // Setting this to the start of the code allows skipping details like
        // method bodies (which we don't care about here).
        parser.setFocalPosition(0);
        break;
      case ClassMembers:
        parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
        break;
      case CodeBlock:
        parser.setKind(ASTParser.K_STATEMENTS);
        break;
      default:
        assert false : "Unexpected snippet type specified.";
    }

    Map<?, ?> parserOptions = JavaCore.getOptions();
    JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, parserOptions);

    parser.setSource(code.toCharArray());
    parser.setCompilerOptions(parserOptions);

    return parser.createAST(null);
  }

  /**
   * Attempts to parse the specified code as a set of class members.
   * @param code the code to be parsed.
   * @param errors the list of errors to be populated if any.
   * @return the list of member names declared. Empty list if nothing was declared, and null if
   *   this code block could not be parsed as a compilation unit.
   */
  @SuppressWarnings("unchecked")
  private List<SnippetCodeMember> parseAsClassMembers(String code, List<String> errors) {
    ASTNode ast = parseCode(code, SnippetType.ClassMembers);
    if (!(ast instanceof TypeDeclaration)) {
      return null;
    }

    TypeDeclaration typeDeclaration = (TypeDeclaration)ast;
    List<SnippetCodeMember> classMembers = new ArrayList<SnippetCodeMember>();

    List<BodyDeclaration> memberDeclarations = typeDeclaration.bodyDeclarations();
    for (BodyDeclaration member : memberDeclarations) {
      switch (member.getNodeType()) {
        case ASTNode.FIELD_DECLARATION:
          FieldDeclaration fieldDeclaration = (FieldDeclaration)member;
          String type = fieldDeclaration.getType().toString();

          for (Object fragment: fieldDeclaration.fragments()) {
            VariableDeclarationFragment varDeclaration = (VariableDeclarationFragment)fragment;
            SnippetCodeMember fieldMember =
                SnippetCodeMember.createField(varDeclaration.getName().getIdentifier(), type);

            classMembers.add(fieldMember);
          }

          break;
        case ASTNode.METHOD_DECLARATION:
          MethodDeclaration methodDeclaration = (MethodDeclaration)member;
          if (methodDeclaration.isConstructor()) {
            errors.add(SnippetParser.ERROR_CTOR_NOT_SUPPORTED);
          }
          else {
            SnippetCodeMember methodMember =
                SnippetCodeMember.createMethod(methodDeclaration.getName().getIdentifier(),
                                               methodDeclaration.toString());
            classMembers.add(methodMember);
          }
          break;
        case ASTNode.INITIALIZER:
          Initializer initializer = (Initializer)member;
          if (((initializer.getModifiers() & Modifier.STATIC) == 0) &&
              (memberDeclarations.size() == 1)) {
            // Special case - what looks like a single class initializer should instead be
            // recognized as a code block.
            return null;
          }
          errors.add(SnippetParser.ERROR_INITIALIZER_NOT_SUPPORTED);
          break;
        case ASTNode.TYPE_DECLARATION:
        case ASTNode.ENUM_DECLARATION:
        case ASTNode.ANNOTATION_TYPE_DECLARATION:
          errors.add(SnippetParser.ERROR_NESTED_TYPES_NOT_SUPPORTED);
          break;
        case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
          errors.add(SnippetParser.ERROR_ANNOTATIONTYPE_MEMBER_NOT_SUPPORTED);
          break;
        case ASTNode.ENUM_CONSTANT_DECLARATION:
          errors.add(SnippetParser.ERROR_ENUM_MEMBER_NOT_SUPPORTED);
          break;
        default:
          errors.add(SnippetParser.ERROR_NOT_SUPPORTED);
          break;
      }
    }

    return classMembers;
  }

  /**
   * Attempts to parse the specified code as a set of statements making up a code block.
   * @param code the code to be parsed.
   * @param errors the list of errors to be populated if any.
   */
  private void parseAsCodeBlock(String code, List<String> errors) {
    CompilationUnit compilationUnit = null;

    ASTNode ast = parseCode(code, SnippetType.CodeBlock);
    while (ast != null) {
      if (ast instanceof CompilationUnit) {
        compilationUnit = (CompilationUnit)ast;
        break;
      }

      ast = ast.getParent();
    }

    if (compilationUnit != null) {
      if (compilationUnit.getProblems().length != 0) {
        for (IProblem problem : compilationUnit.getProblems()) {
          errors.add(String.format("[%d]: %s",
                                   problem.getSourceLineNumber(), problem.getMessage()));
        }
      }
    }
    else {
      errors.add(SnippetParser.ERROR_COULD_NOT_PARSE);
    }
  }

  /**
   * Attempts to parse the specified code as a compilation unit.
   * @param code the code to be parsed.
   * @param errors the list of errors to be populated if any.
   * @return the name of the top-level class in the code. Empty string if one wasn't declared, and
   *  null if this code block could not be parsed as a compilation unit.
   */
  @SuppressWarnings("unchecked")
  private String parseAsCompilationUnit(String code, List<String> errors) {
    ASTNode ast = parseCode(code, SnippetType.CompilationUnit);

    if (ast instanceof CompilationUnit) {
      CompilationUnit compilationUnit = (CompilationUnit)ast;
      if (compilationUnit.getProblems().length == 0) {
        List<AbstractTypeDeclaration> types = compilationUnit.types();

        String firstName = null;
        String firstPublicName = null;

        for (AbstractTypeDeclaration typeDeclaration : types) {
          if (firstName == null) {
            firstName = typeDeclaration.getName().getIdentifier();
          }

          if ((typeDeclaration.getModifiers() & Modifier.PUBLIC) != 0) {
            firstPublicName = typeDeclaration.getName().getIdentifier();
          }
        }

        if (firstPublicName != null) {
          return firstPublicName;
        }
        else if (firstName != null) {
          return firstName;
        }
        else {
          errors.add(SnippetParser.ERROR_MISSING_TYPE_DECLARATION);
          return "";
        }
      }
    }

    return null;
  }
}
