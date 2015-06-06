package org.jboss.forge.addon.docker.facets;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.docker.facets.DockerFacet;
import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerFacetImplTest
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
   private FacetFactory facetFactory;

   @Inject
   ResourceFactory rf;

   @Test
   public void testDockerFacet() throws Exception
   {
      Project project = projectFactory.createTempProject();
      DockerFacet dockerFacet = facetFactory.install(project, DockerFacet.class);
      Assert.assertTrue((dockerFacet.isInstalled()));
      DockerFileResource dockerfileResource = dockerFacet.getDockerfileResource();
      Assert.assertEquals("#Dockerfile for your project", dockerfileResource.getContents());
      

      File f = File.createTempFile("Dockerfile", "");
      @SuppressWarnings("unchecked")
      FileResource<?> resource = rf.create(FileResource.class, f);
      resource.setContents("#new Dockerfile");
      dockerFacet.setDockerfile(resource.getUnderlyingResourceObject());
      dockerfileResource = dockerFacet.getDockerfileResource();
      Assert.assertEquals("#new Dockerfile", dockerFacet.getDockerfileResource().getContents());
      
   }

}
