package org.jboss.forge.addon.docker.facets;

import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * If installed this {@link Facet} that installs a Dockerfile for your project.
 * 
 * @author <a href="mailto:devanshu911@gmail.com">Devanshu Singh</a>
 */

public interface DockerFacet extends ProjectFacet
{
   /**
    * Return the {@link DockerFileResource} installed with this facet.
    * @return {@link DockerFileResource} installed.
    */
   DockerFileResource getDockerfileResource();

   /**
    * Set the {@link DockerFileResource} installed by this facet with the {@link DockerFileResource} passed.
    * 
    * @param dockerFileResource {@link DockerFileResource} whose contents are set to the facet Dockerfile.
    */
   void setDockerFileResource(DockerFileResource dockerFileResource);

}
