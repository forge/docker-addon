package org.jboss.forge.addon.docker.resource;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.docker.validation.DockerfileLinter;
import org.jboss.forge.addon.docker.validation.DockerfileValidationResult;
import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.docker.DockerDescriptor;

class DockerFileResourceImpl extends AbstractFileResource<DockerFileResource> implements DockerFileResource
{

   public DockerFileResourceImpl(ResourceFactory factory, File file)
   {
      super(factory, file);

   }

   @Override
   public DockerFileResource createFrom(File file)
   {
      return new DockerFileResourceImpl(getResourceFactory(), file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public DockerfileValidationResult lint()
   {
      return new DockerfileLinter(getResourceFactory()).lint(this);
   }

   public DockerfileValidationResult lint(Resource<?> ruleFile)
   {
      return new DockerfileLinter(getResourceFactory()).lint(this, ruleFile);
   }

   @Override
   public DockerDescriptor getDockerDescriptor()
   {
      DescriptorImporter<DockerDescriptor> importer = Descriptors.importAs(DockerDescriptor.class);
      DockerDescriptor descriptor = importer.fromString(getContents());
      return descriptor;
   }

   @Override
   public DockerFileResource setContents(DockerDescriptor descriptor)
   {
      String contents = descriptor.exportAsString();
      setContents(contents);
      return this;
   }

}
