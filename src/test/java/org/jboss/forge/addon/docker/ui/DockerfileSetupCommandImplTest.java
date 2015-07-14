package org.jboss.forge.addon.docker.ui;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.docker.facets.DockerFacet;
import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerfileSetupCommandImplTest
{

   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private UITestHarness testHarness;

   private Project project;

   @Before
   public void setup() throws Exception
   {
      project = projectFactory.createTempProject();
      facetFactory.install(project, DockerFacet.class);
      facetFactory.install(project, PackagingFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);
      facetFactory.install(project, ResourcesFacet.class);
   }

   @Test
   public void testDockerfileSetup() throws Exception
   {
      MavenModelResource pom = project.getRoot().reify(DirectoryResource.class).getChild("pom.xml")
               .reify(MavenModelResource.class);

      pom.setContents(pomString);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource1 = javaSourceFacet.getJavaResource("docker.addon.test.war.HelloBean");
      JavaResource javaResource2 = javaSourceFacet.getJavaResource("docker.addon.test.war.PropertiesBean");
      JavaResource javaResource3 = javaSourceFacet.getJavaResource("docker.addon.test.war.TestServlet");

      javaResource1.setContents(helloBeanString);
      javaResource2.setContents(propertiesBeanString);
      javaResource3.setContents(testServletString);

      final ResourcesFacet resourcesFacet = project.getFacet(ResourcesFacet.class);
      FileResource<?> res = resourcesFacet.getResource("app.properties");
      res.setContents(appPropertiesString);

      DockerFileResource dockerfileResource = project.getFacet(DockerFacet.class).getDockerfileResource();

      try (CommandController commandController = testHarness.createCommandController(DockerfileSetupCommandImpl.class,
               project.getRoot()))
      {
         commandController.initialize();
         Result result = commandController.execute();

         Assert.assertEquals("Done! Dockerfile content created successfully.", result.getMessage());
         Assert.assertEquals("#Dockerfile for your project\n" +
                  "FROM jboss/wildfly\n" +
                  "COPY target/docker-addon-test-war.war /opt/wildfly/standalone/deployments/",
                  dockerfileResource.getContents());

         Assert.assertTrue(project.getRoot().reify(DirectoryResource.class).getChildDirectory(
                  "target").exists());

         DirectoryResource targetFolder = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
                  "target");

         Assert.assertTrue(targetFolder.getChild("docker-addon-test-war.war").exists());
      }

   }

   private String appPropertiesString = "title.message=docker-addon-test-war Servlet\n" +
            "hello.message=Hello, world";

   private String pomString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n"
            +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "    <groupId>org.</groupId>\n" +
            "    <artifactId>docker-addon-test-war</artifactId>\n" +
            "    <packaging>war</packaging>\n" +
            "    <version>1.0.0-SNAPSHOT</version>\n" +
            "    <name>Test War for Docker Addon JBoss Forge 2</name>\n" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>javax</groupId>\n" +
            "            <artifactId>javaee-api</artifactId>\n" +
            "            <version>7.0</version>\n" +
            "            <scope>provided</scope>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n" +
            "       \n" +
            "    <build>\n" +
            "        <finalName>docker-addon-test-war</finalName>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>org.apache.maven.plugins</groupId>\n" +
            "                <artifactId>maven-war-plugin</artifactId>\n" +
            "                <version>2.2</version>\n" +
            "                <configuration>                      \n" +
            "                    <failOnMissingWebXml>false</failOnMissingWebXml>\n" +
            "                </configuration>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>";

   private String helloBeanString = "package docker.addon.test.war;\n" +
            "\n" +
            "import javax.ejb.Stateless;\n" +
            "import javax.ejb.EJB;\n" +
            "import javax.annotation.PostConstruct;\n" +
            "import javax.annotation.PreDestroy;\n" +
            "\n" +
            "@Stateless\n" +
            "public class HelloBean {\n" +
            "\n" +
            "    @EJB \n" +
            "    private PropertiesBean propertiesBean;\n" +
            "\n" +
            "    @PostConstruct\n" +
            "    private void init() {\n" +
            "  System.out.println(\"In HelloBean(Stateless)::init()\");\n" +
            "    }\n" +
            "\n" +
            "    public String sayHello() {\n" +
            "  String message = propertiesBean.getProperty(\"hello.message\");\n" +
            "  return message;\n" +
            "    }\n" +
            "\n" +
            "    @PreDestroy\n" +
            "    private void destroy() {\n" +
            "        System.out.println(\"In HelloBean(Stateless)::destroy()\");\n" +
            "    }\n" +
            "\n" +
            "}";

   private String propertiesBeanString = "package docker.addon.test.war;\n" +
            "\n" +
            "import javax.ejb.Singleton;\n" +
            "import javax.ejb.Startup;\n" +
            "import javax.ejb.EJBException;\n" +
            "import javax.annotation.PostConstruct;\n" +
            "import javax.annotation.PreDestroy;\n" +
            "\n" +
            "import java.util.Properties;\n" +
            "import java.io.InputStream;\n" +
            "import java.util.concurrent.atomic.AtomicInteger;\n" +
            "\n" +
            "@Singleton\n" +
            "@Startup\n" +
            "public class PropertiesBean {\n" +
            "\n" +
            "    private Properties props;\n" +
            "    private int accessCount = 0;\n" +
            "\n" +
            "    @PostConstruct\n" +
            "    private void startup() {\n" +
            "\n" +
            "  System.out.println(\"In PropertiesBean(Singleton)::startup()\");\n" +
            "\n" +
            "  try {\n" +
            "\n" +
            "            InputStream propsStream = \n" +
            "     PropertiesBean.class.getResourceAsStream(\"/app.properties\");\n" +
            "            props = new Properties();\n" +
            "            props.load(propsStream);\n" +
            "\n" +
            "        } catch(Exception e) {\n" +
            "      throw new EJBException(\"PropertiesBean initialization error\", e);\n" +
            "        }\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    public String getProperty(String name) {\n" +
            "  accessCount++;\n" +
            "  return props.getProperty(name);\n" +
            "    }\n" +
            "\n" +
            "    public int getAccessCount() {\n" +
            "  return accessCount;\n" +
            "    }\n" +
            "\n" +
            "    @PreDestroy\n" +
            "    private void shutdown() {\n" +
            "        System.out.println(\"In PropertiesBean(Singleton)::shutdown()\");\n" +
            "    }\n" +
            "\n" +
            "}";

   private String testServletString = "package docker.addon.test.war;\n" +
            "\n" +
            "import java.io.IOException;\n" +
            "import java.io.PrintWriter;\n" +
            "import java.util.Enumeration;\n" +
            "\n" +
            "import javax.servlet.ServletConfig;\n" +
            "import javax.servlet.ServletException;\n" +
            "import javax.servlet.annotation.WebInitParam;\n" +
            "import javax.servlet.annotation.WebServlet;\n" +
            "import javax.servlet.http.HttpServlet;\n" +
            "import javax.servlet.http.HttpServletRequest;\n" +
            "import javax.servlet.http.HttpServletResponse;\n" +
            "\n" +
            "import javax.ejb.EJB;\n" +
            "\n" +
            "/**\n" +
            " * \n" +
            " *\n" +
            " */\n" +
            "@WebServlet(name=\"TestServlet\",\n" +
            "      urlPatterns={\"/\"})\n" +
            "public class TestServlet extends HttpServlet {\n" +
            "\n" +
            "    private @EJB PropertiesBean propertiesBean;\n" +
            "    private @EJB HelloBean helloBean;\n" +
            "\n" +
            "    @Override\n" +
            "    public void init(ServletConfig config) throws ServletException {\n" +
            "        super.init(config);\n" +
            "  System.out.println(\"In TestServlet::init()\");\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void service(HttpServletRequest req, HttpServletResponse res)\n" +
            "            throws IOException, ServletException {\n" +
            "\n" +
            "        PrintWriter writer = res.getWriter();\n" +
            "\n" +
            "  String titleMsg = propertiesBean.getProperty(\"title.message\");\n" +
            "        writer.println(titleMsg);\n" +
            "\n" +
            "  String helloMsg = helloBean.sayHello();\n" +
            "  writer.println(\"HelloBean says : \" + helloMsg);\n" +
            "\n" +
            "  int numPropertyAccesses = propertiesBean.getAccessCount();\n" +
            "  writer.println(\"Singleton property access count = \" + \n" +
            "            numPropertyAccesses);\n" +
            "  \n" +
            "    }\n" +
            "}";

}
