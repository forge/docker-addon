package org.jboss.forge.addon.docker.ui;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerfileSetupCommandTest
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
   private ProjectFactory projectFactory;

   @Inject
   private UITestHarness testHarness;

   private Project project;
   private CommandController commandController;

   @Before
   public void setup() throws Exception
   {
      project = projectFactory.createTempProject();
      commandController = testHarness.createCommandController(DockerfileSetupCommandImpl.class, project.getRoot());
      commandController.initialize();
   }

   @Test
   public void testDockerfileSetup() throws Exception
   {
      Result result = commandController.execute();
      Assert.assertTrue(project.getRoot().reify(DirectoryResource.class).getChild("Dockerfile").exists());
      Assert.assertEquals("Dockerfile has been installed.", result.getMessage());
   }

   @Test
   public void testDockerfileCalledTwice() throws Exception
   {
      commandController.execute();
      Assert.assertTrue(project.getRoot().reify(DirectoryResource.class).getChild("Dockerfile").exists());

      commandController.initialize();
      Result result = commandController.execute();
      Assert.assertTrue(project.getRoot().reify(DirectoryResource.class).getChild("Dockerfile").exists());
      Assert.assertEquals("Dockerfile has been installed.", result.getMessage());
   }

   @After
   public void tearDown() throws Exception
   {
      project.getRoot().delete(true);
   }

}
