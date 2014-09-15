// SnippetParserCompilationTests.java
//

package ijava.shell.compiler;

import org.junit.*;

public final class SnippetParserCompilationTests {

  @Test
  public void testTopLevelClass() {
    String code = "public class Foo { }";

    Snippet snippet = null;

    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CompilationUnit, snippet.getType());
    Assert.assertEquals("Foo", snippet.getClassName());
  }

  @Test
  public void testTopLevelClasses() {
    String code = "public class Foo { } \n class Bar { }";

    Snippet snippet = null;

    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CompilationUnit, snippet.getType());
    Assert.assertEquals("Foo", snippet.getClassName());
  }

  @Test
  public void testNonPublicClass() {
    String code = "class Foo { }";

    Snippet snippet = null;

    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CompilationUnit, snippet.getType());
    Assert.assertEquals("Foo", snippet.getClassName());
  }
}
