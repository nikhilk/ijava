// ShellHelpers.java
//

package ijava;

import ijava.data.*;

/**
 * Various helper methods for use in an ijava shell.
 */
public final class ShellHelpers {

  private ShellHelpers() {
  }

  public static HTML html(String markup) {
    return new HTML(markup);
  }

  public static void print(boolean b) {
    System.out.print(b);
  }

  public static void print(char c) {
    System.out.print(c);
  }

  public static void print(int i) {
    System.out.print(i);
  }

  public static void print(long n) {
    System.out.print(n);
  }

  public static void print(double d) {
    System.out.print(d);
  }

  public static void print(Object o) {
    System.out.print(o);
  }

  public static void print(String s) {
    System.out.print(s);
  }

  public static void printf(String s, Object... args) {
    System.out.printf(s, args);
  }

  public static void println(boolean b) {
    System.out.println(b);
  }

  public static void println(char c) {
    System.out.println(c);
  }

  public static void println(int i) {
    System.out.println(i);
  }

  public static void println(long n) {
    System.out.println(n);
  }

  public static void println(double d) {
    System.out.println(d);
  }

  public static void println(Object o) {
    System.out.println(o);
  }

  public static void println(String s) {
    System.out.println(s);
  }

  public static JavaScript script(String script) {
    return new JavaScript(script);
  }
}
