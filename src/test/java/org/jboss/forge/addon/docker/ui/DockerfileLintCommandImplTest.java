package org.jboss.forge.addon.docker.ui;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerfileLintCommandImplTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .add(new FileAsset(new File("src/test/resources/org/jboss/forge/addon/docker/ui/Dockerfile")),
                        "org/jboss/forge/addon/docker/ui/Dockerfile")
               .add(new FileAsset(new File("src/test/resources/org/jboss/forge/addon/docker/ui/default_rules.yaml")),
                        "org/jboss/forge/addon/docker/ui/default_rules.yaml");
   }

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private UITestHarness testHarness;

   private CommandController commandController;

   @Before
   public void setup() throws Exception
   {
      commandController = testHarness.createCommandController(DockerfileLintCommandImpl.class);
   }

   @Test
   public void testDockerfileLint() throws Exception
   {

      Assert.assertNotNull(getClass().getResource("default_rules.yaml"));
      Assert.assertNotNull(getClass().getResource("Dockerfile"));

      DockerFileResource dockerfileResource = resourceFactory.create(DockerFileResource.class,
               File.createTempFile("Dockerfile", ""));
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
               result.getMessage(), "Linted Sucessfully");

   }

   @Test
   public void testDockerfileLintWithNoRuleFile() throws Exception
   {

      Assert.assertNotNull(getClass().getResource("default_rules.yaml"));
      Assert.assertNotNull(getClass().getResource("Dockerfile"));

      DockerFileResource resource = resourceFactory.create(DockerFileResource.class,
               File.createTempFile("Dockerfile", ""));
      resource.createNewFile();
      Assert.assertTrue(resource.exists());
      resource.setContents(getClass().getResource("Dockerfile").openStream());

      commandController.initialize();
      commandController.setValueFor("dockerfile", resource);
      Result result = commandController.execute();
      Assert.assertFalse(result instanceof Failed);

      Assert.assertEquals(
               result.getMessage(), "Linted Sucessfully");

   }

}
