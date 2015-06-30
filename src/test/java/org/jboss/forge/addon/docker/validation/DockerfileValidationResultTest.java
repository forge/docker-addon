package org.jboss.forge.addon.docker.validation;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerfileValidationResultTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:docker-addon"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:resources"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")

   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:docker-addon"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources")

               );
   }

   @Inject
   DockerfileValidationResult result;

   @Test
   public void testDockerfileValidationResult()
   {

      Assert.assertEquals(result.toString(), "Validation Results\nErrors: 0\nWarn: 0\nInfo: 0\n");

      result.addError("Error 1", "This line has Error", 1);
      result.addWarn("Warn 1", "This line has Warn", 2);
      result.addInfo("Info 1", "This line has Info", 3);
      result.addError("Error 2", "This line has Error", 4);

      Assert.assertEquals(result.getErrors(), 2);
      Assert.assertEquals(result.getWarn(), 1);
      Assert.assertEquals(result.getInfo(), 1);

      String resultString = "Validation Results\n" +
               "Errors: 2\n" +
               "Warn: 1\n" +
               "Info: 1\n" +
               "\n" +
               "Type: ERROR\n" +
               "Message: Error 1\n" +
               "Line=This line has Error\n" +
               "Line Number:1\n" +
               "\n" +
               "Type: WARN\n" +
               "Message: Warn 1\n" +
               "Line=This line has Warn\n" +
               "Line Number:2\n" +
               "\n" +
               "Type: INFO\n" +
               "Message: Info 1\n" +
               "Line=This line has Info\n" +
               "Line Number:3\n" +
               "\n" +
               "Type: ERROR\n" +
               "Message: Error 2\n" +
               "Line=This line has Error\n" +
               "Line Number:4\n" +
               "";

      Assert.assertEquals(result.toString(), resultString);

   }

}
