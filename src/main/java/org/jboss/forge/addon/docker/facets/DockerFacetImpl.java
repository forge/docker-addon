package org.jboss.forge.addon.docker.facets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;

public class DockerFacetImpl extends AbstractFacet<Project> implements DockerFacet
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
   public DockerFileResource getDockerfile()
   {

      return getFaceted().getRoot().getChild("Dockerfile").reify(DockerFileResource.class);
   }

   @Override
   public void setDockerfile(File file)
   {

      DockerFileResource dockerfile = getFaceted().getRoot().getChild("Dockerfile")
               .reify(DockerFileResource.class);

      try
      {
         dockerfile.setContents(new FileInputStream(file));
      }
      catch (FileNotFoundException e)
      {
      }

   }

   public void setDockerFile(DockerFileResource drf)
   {
      DockerFileResource dockerfile = getFaceted().getRoot().getChild("Dockerfile")
               .reify(DockerFileResource.class);

      dockerfile.setContents(drf.getContents());

   }

}
