package org.jboss.forge.addon.docker.facets;

import java.io.File;

import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.projects.ProjectFacet;

public interface DockerFacet extends ProjectFacet
{
   DockerFileResource getDockerfileResource();

   void setDockerfile(File dockerfile);
   
   void setDockerFileResource(DockerFileResource dockerFileresource);
    
}
