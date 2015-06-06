package org.jboss.forge.addon.docker.facets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
   public DockerFileResource getDockerfileResource()
   {

      return getFaceted().getRoot().getChild("Dockerfile").reify(DockerFileResource.class);
   }

   @Override
   public void setDockerfile(File file)
   {

      DockerFileResource dockerfile = getFaceted().getRoot().getChild("Dockerfile")
               .reify(DockerFileResource.class);

      InputStream is = null;
      try
      {
         is = new FileInputStream(file);
      }
      catch (FileNotFoundException e1)
      {
      }
      dockerfile.setContents(is);

      try
      {
         is.close();
      }
      catch (IOException e)
      {
      }

   }

   public void setDockerFileResource(DockerFileResource dockerFileResource)
   {
      DockerFileResource dockerfile = getFaceted().getRoot().getChild("Dockerfile")
               .reify(DockerFileResource.class);

      dockerfile.setContents(dockerFileResource.getContents());

   }

}
