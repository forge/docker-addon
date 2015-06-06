package org.jboss.forge.addon.docker.resource;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

public class DockerFileResourceGenerator implements ResourceGenerator<DockerFileResource, File>
{

   @SuppressWarnings("unchecked")
   @Override
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<DockerFileResource> type,
            File resource)
   {
      return (T) new DockerFileResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<DockerFileResource> type,
            File file)
   {
      return DockerFileResource.class;
   }

   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File)
      {
         return true;
      }
      return false;
   }
}
