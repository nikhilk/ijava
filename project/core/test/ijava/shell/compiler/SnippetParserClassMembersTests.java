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
      snippet = parser.parse(code, 1);
    }
    catch (SnippetException e) {
      Assert.fail(e.getMessage());
    }

    Assert.assertEquals(SnippetType.ClassMembers, snippet.getType());
    Assert.assertEquals(snippet.getClassName(), "__Class1__");
    Assert.assertEquals(1, snippet.getClassMembers().size());
    Assert.assertEquals("i", snippet.getClassMembers().get(0).getName());
    Assert.assertTrue(snippet.getClassMembers().get(0).isField());
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

    Assert.assertEquals(SnippetType.ClassMembers, snippet.getType());
    Assert.assertEquals(1, snippet.getClassMembers().size());
    Assert.assertEquals("doSomething", snippet.getClassMembers().get(0).getName());
    Assert.assertTrue(snippet.getClassMembers().get(0).isMethod());
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

    Assert.assertEquals(SnippetType.ClassMembers, snippet.getType());
    Assert.assertEquals(3, snippet.getClassMembers().size());
    Assert.assertEquals("i", snippet.getClassMembers().get(0).getName());
    Assert.assertEquals("j", snippet.getClassMembers().get(1).getName());
    Assert.assertEquals("k", snippet.getClassMembers().get(2).getName());
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

    Assert.assertEquals(SnippetType.ClassMembers, snippet.getType());
    Assert.assertEquals(3, snippet.getClassMembers().size());
    Assert.assertEquals("doSomething", snippet.getClassMembers().get(0).getName());
    Assert.assertEquals("implementation", snippet.getClassMembers().get(1).getName());
    Assert.assertEquals("_data", snippet.getClassMembers().get(2).getName());
    Assert.assertTrue(snippet.getClassMembers().get(0).isMethod());
    Assert.assertTrue(snippet.getClassMembers().get(1).isMethod());
    Assert.assertTrue(snippet.getClassMembers().get(2).isField());
  }
}
