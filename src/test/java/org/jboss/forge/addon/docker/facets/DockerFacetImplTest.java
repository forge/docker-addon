package org.jboss.forge.addon.docker.facets;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DockerFacetImplTest
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
   ResourceFactory rf;

   @Test
   public void testDockerFacet() throws Exception
   {
      Project project = projectFactory.createTempProject();
      DockerFacet dockerFacet = facetFactory.install(project, DockerFacet.class);
      Assert.assertTrue((dockerFacet.isInstalled()));
      DockerFileResource dockerfileResource = dockerFacet.getDockerfileResource();
      Assert.assertEquals("#Dockerfile for your project", dockerfileResource.getContents());

   }

}
