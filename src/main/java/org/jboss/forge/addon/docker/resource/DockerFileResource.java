package org.jboss.forge.addon.docker.resource;

import org.jboss.forge.addon.docker.validation.DockerfileValidationResult;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.shrinkwrap.descriptor.api.docker.DockerDescriptor;

public interface DockerFileResource extends FileResource<DockerFileResource>
{

   DockerfileValidationResult verify();

   DockerfileValidationResult verify(FileResource<?> ruleFile);

   DockerDescriptor getDockerDescriptor();

  // DockerFileResource setContents(DockerFileResource resource);

   DockerFileResource setContents(DockerDescriptor descriptor);

}
