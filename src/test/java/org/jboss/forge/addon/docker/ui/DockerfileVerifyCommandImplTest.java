package org.jboss.forge.addon.docker.ui;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.docker.ui.DockerfileVerifyCommandImpl;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerfileVerifyCommandImplTest
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
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/docker/ui/dockerfile")),
                        "org/jboss/forge/addon/docker/ui/Dockerfile")
               .add(new FileAsset(new File(
                        "src/test/resources/org/jboss/forge/addon/docker/ui/default_rules.yaml")),
                        "org/jboss/forge/addon/docker/ui/default_rules.yaml")
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
   private ResourceFactory resourceFactory;

   @Inject
   private UITestHarness testHarness;

   private CommandController commandController;

   @Before
   public void setup() throws Exception
   {
      commandController = testHarness.createCommandController(DockerfileVerifyCommandImpl.class);
   }

   @Test
   public void testDockerfileValidation() throws Exception
   {

      Assert.assertNotNull(getClass().getResource("default_rules.yaml"));
      Assert.assertNotNull(getClass().getResource("Dockerfile"));

      DockerFileResource dockerfileResource = resourceFactory.create(DockerFileResource.class, File.createTempFile("Dockerfile", ""));
      dockerfileResource.createNewFile();
      Assert.assertTrue(dockerfileResource.exists());
      dockerfileResource.setContents(getClass().getResource("Dockerfile").openStream());

      @SuppressWarnings("unchecked")
      FileResource<?> ruleFile = resourceFactory.create(FileResource.class, File.createTempFile("rules", "yaml"));
      ruleFile.createNewFile();
      Assert.assertTrue(ruleFile.exists());
      ruleFile.setContents(getClass().getResource("default_rules.yaml").openStream());

      commandController.initialize();
      commandController.setValueFor("dockerfile", dockerfileResource);
      commandController.setValueFor("rulefile", ruleFile);
      Result result = commandController.execute();
      Assert.assertFalse(result instanceof Failed);
      Assert.assertEquals(
               result.getMessage(),
               "Lint Results: \nValidation Results\nErrors: 0\nWarn: 1\nInfo: 2\n\nType: info\nMessage: There is no \'EXPOSE\' instruction.Without exposed ports how will the service of the container be accessed?.\nReference --> \nhttps://docs.docker.com/reference/builder/#expose\n\nType: info\nMessage: There is no \'ENTRYPOINT\' instruction.None.\nReference --> \nhttps://docs.docker.com/reference/builder/#entrypoint\n\nType: warn\nMessage: No \'USER\' instruction.The process(es) within the container may run as root and RUN instructions my be run as root.\nReference --> \nhttps://docs.docker.com/reference/builder/#user\n");

   }
   
   @Test
   public void testDockerfileValidationWithNoRuleFile() throws Exception
   {

      Assert.assertNotNull(getClass().getResource("default_rules.yaml"));
      Assert.assertNotNull(getClass().getResource("Dockerfile"));

      DockerFileResource resource = resourceFactory.create(DockerFileResource.class, File.createTempFile("Dockerfile", ""));
      resource.createNewFile();
      Assert.assertTrue(resource.exists());
      resource.setContents(getClass().getResource("Dockerfile").openStream());


      commandController.initialize();
      commandController.setValueFor("dockerfile", resource);
      Result result = commandController.execute();
      Assert.assertFalse(result instanceof Failed);
      Assert.assertEquals(
               result.getMessage(),"Lint Results: \nValidation Results\nErrors: 0\nWarn: 0\nInfo: 0\n");

   }

}
