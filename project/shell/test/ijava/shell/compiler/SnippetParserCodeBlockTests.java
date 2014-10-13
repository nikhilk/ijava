// SnippetParserCodeBlockTests.java
//

package ijava.shell.compiler;

import org.junit.*;

public final class SnippetParserCodeBlockTests {

  @Test
  public void testStatement() {
    String code = "System.out.println(\"Hello\");";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CodeBlock, snippet.getType());
    Assert.assertEquals(snippet.getClassName(), "__Class1__");
  }

  @Test
  public void testStatementBlock() {
    String code = "{ System.out.println(\"Hello\"); }";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CodeBlock, snippet.getType());
  }

  @Test
  public void testStatementControlFlow() {
    String code = "if (true) { System.out.println(\"Hello\"); }";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CodeBlock, snippet.getType());
  }

  @Test
  public void testStatementSequence() {
    String code = "System.out.println(\"Hello\"); System.out.println(\"Goodbye\");";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CodeBlock, snippet.getType());
  }
}
