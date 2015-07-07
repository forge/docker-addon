package org.jboss.forge.addon.docker.facets;

import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;

class DockerFacetImpl extends AbstractFacet<Project> implements DockerFacet
{

   @Override
   public boolean install()
   {
      Project project = getFaceted();
      final DirectoryResource rootDirectory = project.getRoot().reify(DirectoryResource.class);
      final DockerFileResource dockerfile = rootDirectory.getChild("Dockerfile").reify(DockerFileResource.class);
      dockerfile.setContents("#Dockerfile for your project");
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getFaceted().getRoot().getChild("Dockerfile").exists();
   }

   @Override
   public DockerFileResource getDockerfileResource()
   {

      return getFaceted().getRoot().getChild("Dockerfile").reify(DockerFileResource.class);
   }

   public void setDockerFileResource(DockerFileResource dockerFileResource)
   {
      DockerFileResource dockerfile = getFaceted().getRoot().getChild("Dockerfile")
               .reify(DockerFileResource.class);

      dockerfile.setContents(dockerFileResource.getContents());

   }

}
