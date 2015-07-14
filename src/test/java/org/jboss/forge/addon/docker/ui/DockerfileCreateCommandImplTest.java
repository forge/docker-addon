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
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerfileCreateCommandImplTest
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
   private UITestHarness testHarness;

   private Project project;

   @Before
   public void setup() throws Exception
   {
      project = projectFactory.createTempProject();
   }

   @Test
   public void testDockerfileSetup() throws Exception
   {
      try (CommandController commandController = testHarness.createCommandController(DockerfileCreateCommandImpl.class,
               project.getRoot()))
      {
         commandController.initialize();
         Result result = commandController.execute();
         Assert.assertTrue(project.getRoot().reify(DirectoryResource.class).getChild("Dockerfile").exists());
         Assert.assertEquals("Dockerfile has been installed.", result.getMessage());
      }
   }

   @Test
   public void testDockerfileSetupCalledTwice() throws Exception
   {
      try (CommandController commandController = testHarness.createCommandController(DockerfileCreateCommandImpl.class,
               project.getRoot()))
      {
         commandController.initialize();
         commandController.execute();
      }
      Assert.assertTrue(project.getRoot().reify(DirectoryResource.class).getChild("Dockerfile").exists());

      try (CommandController commandController = testHarness.createCommandController(DockerfileCreateCommandImpl.class,
               project.getRoot()))
      {
         commandController.initialize();
         Result result = commandController.execute();
         Assert.assertTrue(project.getRoot().reify(DirectoryResource.class).getChild("Dockerfile").exists());
         Assert.assertEquals("Dockerfile has been installed.", result.getMessage());
      }
   }

   @After
   public void tearDown() throws Exception
   {
      project.getRoot().delete(true);
   }

}
