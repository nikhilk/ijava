// MavenRepository.java
//

package ijava.shell;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Provides the ability to resolve maven artifacts.
 * 
 * TODO: Support maven repositories other than maven central.
 * TODO: Support resolving maven artifacts without transitive closure.
 */
public final class MavenRepository {

  private static final String POM_TEMPLATE =
      "<project xmlns='http://maven.apache.org/POM/4.0.0' " +
          "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
          "xsi:schemaLocation='http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd'>\n" +
          "  <modelVersion>4.0.0</modelVersion>\n" +
          "  <groupId>ijava</groupId>\n" +
          "  <artifactId>temp</artifactId>\n" +
          "  <version>1</version>\n" +
          "  <dependencies>\n" +
          "    <dependency>\n" +
          "      <groupId>%s</groupId>\n" +
          "      <artifactId>%s</artifactId>\n" +
          "      <version>%s</version>\n" +
          "    </dependency>\n" +
          "  </dependencies>\n" +
          "</project>\n";

  private static final String MavenCentral = "central::default::http://repo1.maven.apache.org/maven2";
  private static final String MavenProject = "/tmp/ijava/maven/pom.xml";
  private static final String MavenTarget = "org.apache.maven.plugins:maven-dependency-plugin:2.8:resolve";
  private static final String MavenRepo;
  private static final String MavenPath;

  static {
    MavenRepo = MavenRepository.findMavenRepository();
    MavenPath = MavenRepository.findMavenExecutable();
  }

  /**
   * Creates the pom.xml file required by maven when resolving dependencies.
   * @param xml the XML content of the project file.
   * @throws IOException if there was an error creating the project file.
   */
  private void createProjectFile(String xml) throws IOException {
    Path projectFile = Paths.get(MavenRepository.MavenProject);
    Path projectDirectory = projectFile.getParent();

    Files.createDirectories(projectDirectory);
    Files.write(projectFile, xml.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
  }

  /**
   * Locates the maven executable on the path.
   * @return the full path to the maven executable.
   */
  private static String findMavenExecutable() {
    String[] pathList = System.getenv("PATH").split(File.pathSeparator);

    for (String pathPart : pathList) {
      Path mavenFilePath = Paths.get(pathPart, "mvn");

      if (Files.exists(mavenFilePath) && Files.isExecutable(mavenFilePath)) {
        return mavenFilePath.toString();
      }
    }

    return null;
  }

  /**
   * Locates the maven local repository cache.
   * @return the full path to the maven executable.
   */
  private static String findMavenRepository() {
    Path mavenRepoPath = Paths.get(System.getProperty("user.home"), ".m2", "repository");

    if (Files.exists(mavenRepoPath) && Files.isDirectory(mavenRepoPath)) {
      return mavenRepoPath.toString();
    }

    return null;
  }

  /**
   * Resolves the specified artifact, including its dependencies. The approach taken:
   * - Create a stub pom.xml with artifact to resolve listed as a dependency.
   * - Invoke maven with the dependency:resolve target
   * - Parse the output for the resulting jars, and resolve their locations in the local maven
   *   cache.
   * @param groupId the maven group of the artifact.
   * @param artifactId the maven id of the artifact.
   * @param version the version of the artifact.
   * @return the list of jars that are the result of a resolve.
   */
  public List<String> resolveArtifact(String groupId, String artifactId, String version) {
    if (MavenRepository.MavenPath == null) {
      throw new IllegalStateException("Unable to find maven to resolve maven artifact.");
    }

    // Create the stub pom.xml
    try {
      String projectModel = String.format(MavenRepository.POM_TEMPLATE,
                                          groupId, artifactId, version);
      createProjectFile(projectModel);
    }
    catch (Exception e) {
      throw new IllegalStateException("Unable to create maven project to resolve maven artifact.");
    }

    List<String> jars = new ArrayList<String>();
    try {
      String[] mavenCommand = new String[] {
        MavenRepository.MavenPath,
        MavenRepository.MavenTarget,
        "-DremoteRepositories=" + MavenRepository.MavenCentral,
        "-DexcludeTransitive=false",
        "-DincludeScope=runtime",
        "--file", MavenRepository.MavenProject
      };

      // Run maven, specifically for the dependency:resolve target
      Process process = Runtime.getRuntime().exec(mavenCommand);
      BufferedReader processOutputReader =
          new BufferedReader(new InputStreamReader(process.getInputStream()));

      // Parse the resulting maven spew and resolve jar paths listed for the runtime scope
      String line;
      while ((line = processOutputReader.readLine()) != null) {
        if (!line.startsWith("[INFO]    ") || !line.endsWith(":compile")) {
          continue;
        }

        String[] lineParts = line.substring(10).split(":");
        if ((lineParts.length != 5) || !lineParts[2].equals("jar")) {
          continue;
        }

        String resolvedGroupId = lineParts[0];
        String resolvedArtifactId = lineParts[1];
        String resolvedVersion = lineParts[3];

        Path jarPath = Paths.get(MavenRepository.MavenRepo,
                                 resolvedGroupId.replace('.', File.separatorChar),
                                 resolvedArtifactId,
                                 resolvedVersion,
                                 resolvedArtifactId + "-" + resolvedVersion + ".jar");
        jars.add(jarPath.toString());
      }

      processOutputReader.close();
    }
    catch (Exception e) {
      throw new IllegalStateException("Unable to find maven to resolve maven artifacts.");
    }

    return jars;
  }
}
