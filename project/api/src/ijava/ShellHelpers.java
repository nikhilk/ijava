// ShellHelpers.java
//

package ijava;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
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

  public static byte[] readFileBytes(String file) throws IOException {
    return Files.readAllBytes(Paths.get(file));
  }

  public static List<String> readFileLines(String file) throws IOException {
    return Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
  }

  public static String readFileText(String file) throws IOException {
    return new String(ShellHelpers.readFileBytes(file), StandardCharsets.UTF_8);
  }

  public static JavaScript script(String script) {
    return new JavaScript(script);
  }

  public static void writeFile(String file, byte[] bytes) throws IOException {
    Files.write(Paths.get(file), bytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
  }

  public static void writeFile(String file, List<String> lines) throws IOException {
    Files.write(Paths.get(file), lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
  }

  public static void writeFile(String file, String[] lines) throws IOException {
    Files.write(Paths.get(file), Arrays.asList(lines), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
  }

  public static void writeFile(String file, String text) throws IOException {
    Files.write(Paths.get(file), text.getBytes());
  }
}
