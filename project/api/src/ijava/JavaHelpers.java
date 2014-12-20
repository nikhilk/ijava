// JavaHelpers.java
//

package ijava;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import ijava.data.*;

/**
 * Various helper methods for use in an ijava shell.
 */
public final class JavaHelpers {

  // NOTE: Methods that would be void returning are intentionally setup to return Object, and
  //       return a null value. This is to allow using these methods as an expression.

  private JavaHelpers() {
  }

  public static <K, V> Map.Entry<K, V> entry(K key, V value) {
    return new AbstractMap.SimpleEntry<K, V>(key, value);
  }

  public static HTML html(String markup) {
    return new HTML(markup);
  }

  public static Image image(String url) throws URISyntaxException {
    return new Image(url);
  }

  public static Image image(URI uri) {
    return new Image(uri);
  }

  public static Image image(byte[] data, String mimeType) {
    return new Image(data, mimeType);
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T> list(T... items) {
    return Arrays.asList(items);
  }

  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> map(Map.Entry<K, V>... entries) {
    HashMap<K, V> map = new HashMap<K, V>();
    for (int i = 0; i < entries.length; i++) {
      Map.Entry<K, V> entry = entries[i];
      map.put(entry.getKey(), entry.getValue());
    }

    return map;
  }

  public static Object print(boolean b) {
    System.out.print(b);
    return null;
  }

  public static Object print(char c) {
    System.out.print(c);
    return null;
  }

  public static Object print(int i) {
    System.out.print(i);
    return null;
  }

  public static Object print(long n) {
    System.out.print(n);
    return null;
  }

  public static Object print(double d) {
    System.out.print(d);
    return null;
  }

  public static Object print(Object o) {
    System.out.print(o);
    return null;
  }

  public static Object print(String s) {
    System.out.print(s);
    return null;
  }

  public static Object printf(String s, Object... args) {
    System.out.printf(s, args);
    return null;
  }

  public static Object println(boolean b) {
    System.out.println(b);
    return null;
  }

  public static Object println(char c) {
    System.out.println(c);
    return null;
  }

  public static Object println(int i) {
    System.out.println(i);
    return null;
  }

  public static Object println(long n) {
    System.out.println(n);
    return null;
  }

  public static Object println(double d) {
    System.out.println(d);
    return null;
  }

  public static Object println(Object o) {
    System.out.println(o);
    return null;
  }

  public static Object println(String s) {
    System.out.println(s);
    return null;
  }

  public static byte[] readFileBytes(String file) throws IOException {
    return Files.readAllBytes(Paths.get(file));
  }

  public static List<String> readFileLines(String file) throws IOException {
    return Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
  }

  public static String readFileText(String file) throws IOException {
    return new String(JavaHelpers.readFileBytes(file), StandardCharsets.UTF_8);
  }

  public static JavaScript script(String script) {
    return new JavaScript(script);
  }

  @SuppressWarnings("unchecked")
  public static <T> Set<T> set(T... items) {
    HashSet<T> set = new HashSet<T>();
    for (int i = 0; i < items.length; i++) {
      set.add(items[i]);
    }

    return set;
  }

  public static Object writeFile(String file, byte[] bytes) throws IOException {
    Files.write(Paths.get(file), bytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    return null;
  }

  public static Object writeFile(String file, List<String> lines) throws IOException {
    Files.write(Paths.get(file), lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    return null;
  }

  public static Object writeFile(String file, String[] lines) throws IOException {
    Files.write(Paths.get(file), Arrays.asList(lines), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    return null;
  }

  public static Object writeFile(String file, String text) throws IOException {
    Files.write(Paths.get(file), text.getBytes());
    return null;
  }
}
