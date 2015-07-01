package org.jboss.forge.addon.docker.resource;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.docker.DockerDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerfileResourceImplTest
{

   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().add(new FileAsset(new File(
               "src/test/resources/org/jboss/forge/addon/docker/resource/Dockerfile")),
               "org/jboss/forge/addon/docker/resource/Dockerfile");
   }

   @Inject
   private ResourceFactory resourceFactory;

   @Test
   public void testDockerfileResourceCreation() throws Exception
   {
      String dockerFileContents = "FROM jbossforge\nUSER XX\nRUN /tmp/mysql-setup.sh";

      DescriptorImporter<DockerDescriptor> importer = Descriptors.importAs(DockerDescriptor.class);
      DockerDescriptor descriptor = importer.fromString(dockerFileContents);
      Assert.assertNotNull(descriptor);
      Assert.assertEquals(3, descriptor.getInstructions().size());
      Assert.assertNotNull(descriptor.getFrom());
      Assert.assertNotNull(descriptor.getUser());
      Assert.assertEquals(1, descriptor.getAllRun().size());
      Assert.assertEquals("jbossforge", descriptor.getFrom().getName());
      Assert.assertEquals("XX", descriptor.getUser().getName());
      List<String> parameters = descriptor.getAllRun().get(0).getParameters();
      Assert.assertEquals("/tmp/mysql-setup.sh", parameters.get(0));

      DockerFileResource resource = resourceFactory.create(DockerFileResource.class,
               File.createTempFile("Dockerfile", ""));
      resource.createNewFile();
      resource.setContents(descriptor);

      DockerDescriptor resourceDescriptor = resource.getDockerDescriptor();

      Assert.assertNotNull(resourceDescriptor);
      Assert.assertEquals(3, resourceDescriptor.getInstructions().size());
      Assert.assertNotNull(resourceDescriptor.getFrom());
      Assert.assertNotNull(resourceDescriptor.getUser());
      Assert.assertEquals(1, resourceDescriptor.getAllRun().size());
      Assert.assertEquals("jbossforge", resourceDescriptor.getFrom().getName());
      Assert.assertEquals("XX", resourceDescriptor.getUser().getName());
      List<String> parameter = resourceDescriptor.getAllRun().get(0).getParameters();
      Assert.assertEquals("/tmp/mysql-setup.sh", parameter.get(0));

      Assert.assertEquals(dockerFileContents, resourceDescriptor.exportAsString());

   }

   @Test
   public void testDockerfileResourceCreationFromExistingDockerfile() throws Exception
   {

      Assert.assertNotNull(getClass().getResource("Dockerfile"));

      DockerFileResource dockerfileResource = resourceFactory.create(DockerFileResource.class,
               File.createTempFile("Dockerfile", ""));
      dockerfileResource.createNewFile();
      Assert.assertTrue(dockerfileResource.exists());
      dockerfileResource.setContents(getClass().getResource("Dockerfile").openStream());

      DockerFileResource resource = resourceFactory.create(DockerFileResource.class,
               File.createTempFile("Dockerfile", ""));
      resource.createNewFile();

      resource.setContents(dockerfileResource.getContents());
      Assert.assertEquals(resource.getContents(), dockerfileResource.getContents());

   }

}