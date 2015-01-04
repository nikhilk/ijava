// SnippetParserCodeMembersTests.java
//

package ijava.shell.compiler;

import org.junit.*;

public final class SnippetParserCodeMembersTests {

  @Test
  public void testField() {
    String code = "int i = 10;";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CodeMembers, snippet.getType());
    Assert.assertEquals(snippet.getClassName(), "__Class1__");
    Assert.assertEquals(1, snippet.getCodeMembers().size());
    Assert.assertEquals("i", snippet.getCodeMembers().get(0).getName());
    Assert.assertTrue(snippet.getCodeMembers().get(0).isField());
  }

  @Test
  public void testMethod() {
    String code = "public int doSomething() { return 42; }";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CodeMembers, snippet.getType());
    Assert.assertEquals(1, snippet.getCodeMembers().size());
    Assert.assertEquals("doSomething", snippet.getCodeMembers().get(0).getName());
    Assert.assertTrue(snippet.getCodeMembers().get(0).isMethod());
  }

  @Test
  public void testMultipleFields() {
    String code = "int i = 42, j, k;";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CodeMembers, snippet.getType());
    Assert.assertEquals(3, snippet.getCodeMembers().size());
    Assert.assertEquals("i", snippet.getCodeMembers().get(0).getName());
    Assert.assertEquals("j", snippet.getCodeMembers().get(1).getName());
    Assert.assertEquals("k", snippet.getCodeMembers().get(2).getName());
  }

  @Test
  public void testMultipleMembers() {
    String code = "public int doSomething() { return 42; }\n" +
        "void implementation() { }\n" +
        "int _data;";

    Snippet snippet = null;
    try {
      SnippetParser parser = new SnippetParser();
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.CodeMembers, snippet.getType());
    Assert.assertEquals(3, snippet.getCodeMembers().size());
    Assert.assertEquals("doSomething", snippet.getCodeMembers().get(0).getName());
    Assert.assertEquals("implementation", snippet.getCodeMembers().get(1).getName());
    Assert.assertEquals("_data", snippet.getCodeMembers().get(2).getName());
    Assert.assertTrue(snippet.getCodeMembers().get(0).isMethod());
    Assert.assertTrue(snippet.getCodeMembers().get(1).isMethod());
    Assert.assertTrue(snippet.getCodeMembers().get(2).isField());
  }
}
