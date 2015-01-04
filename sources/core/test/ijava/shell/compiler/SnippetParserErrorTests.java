// SnippetParserErrorTests.java
//

package ijava.shell.compiler;

import org.junit.*;

public final class SnippetParserErrorTests {

  @Test()
  public void testEmptySnippet() {
    String code = "    ";

    try {
      SnippetParser parser = new SnippetParser();
      parser.parse(code, 1);

      Assert.fail("Expected an exception to be raised.");
    }
    catch (SnippetException e) {
    }
  }

  @Test()
  public void testSyntaxError() {
    String code = "pub class Foo { }";

    try {
      SnippetParser parser = new SnippetParser();
      parser.parse(code, 1);

      Assert.fail("Expected an exception to be raised.");
    }
    catch (SnippetException e) {
    }
  }

  @Test()
  public void testUnsupported() {
    String code = "public int doSomething() { return 42; }\n" +
        "class NestedClass { }";

    try {
      SnippetParser parser = new SnippetParser();
      parser.parse(code, 1);

      Assert.fail("Expected an exception to be raised.");
    }
    catch (SnippetException e) {
    }
  }
}
