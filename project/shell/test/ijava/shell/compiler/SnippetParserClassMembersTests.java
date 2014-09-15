// SnippetParserClassMembersTests.java
//

package ijava.shell.compiler;

import org.junit.*;

public final class SnippetParserClassMembersTests {

  @Test
  public void testField() {
    String code = "int i = 10;";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.ClassMembers, snippet.getType());
    Assert.assertEquals(1, snippet.getClassMembers().size());
    Assert.assertTrue(snippet.getClassMembers().containsKey("i"));
  }

  @Test
  public void testMethod() {
    String code = "public int doSomething() { return 42; }";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.ClassMembers, snippet.getType());
    Assert.assertEquals(1, snippet.getClassMembers().size());
    Assert.assertTrue(snippet.getClassMembers().containsKey("doSomething"));
  }

  @Test
  public void testMultipleFields() {
    String code = "int i = 42, j, k;";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.ClassMembers, snippet.getType());
    Assert.assertEquals(3, snippet.getClassMembers().size());
    Assert.assertTrue(snippet.getClassMembers().containsKey("i"));
    Assert.assertTrue(snippet.getClassMembers().containsKey("j"));
    Assert.assertTrue(snippet.getClassMembers().containsKey("k"));
  }

  @Test
  public void testMultipleMembers() {
    String code = "public int doSomething() { return 42; }\n" +
        "void implementation() { }\n" +
        "int _data;";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.ClassMembers, snippet.getType());
    Assert.assertEquals(3, snippet.getClassMembers().size());
    Assert.assertTrue(snippet.getClassMembers().containsKey("doSomething"));
    Assert.assertTrue(snippet.getClassMembers().containsKey("implementation"));
    Assert.assertTrue(snippet.getClassMembers().containsKey("_data"));
  }
}
