package org.jboss.forge.addon.docker.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.docker.validation.DockerfileValidationImpl;
import org.jboss.forge.addon.docker.validation.DockerfileValidationResult;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

public class DockerfileVerifyCommandImpl extends AbstractUICommand implements DockerfileVerifyCommand
{

   @Inject
   @WithAttributes(label = "Dockerfile", description = "The Dockerfile to be verifyed", required = true)
   private UISelectOne<DockerFileResource> dockerfile;

   @Inject
   @WithAttributes(label = "Rule File", description = "The rule file against which the dockerfile should be verified")
   private UISelectOne<FileResource<?>> rulefile;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Dockerfile: Verify")
               .description("Dockerfile Linter")
               .category(Categories.create("Docker"));
   }
   
   @Inject
   DockerfileValidationImpl imp;

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      DockerfileValidationResult result;

      if (!dockerfile.getValue().exists())
         return Results.fail("Dockerfile not found");

      if (dockerfile.getValue().getResourceInputStream() == null)
         return Results.fail("Couldnot parse the dockerfile");

      if ((rulefile.getValue() != null) && (!rulefile.getValue().exists()))
         return Results.fail("Rulefile not found");

      if (rulefile.getValue() == null)
         result = imp.verify(dockerfile.getValue());

      else
         result = imp.verify(dockerfile.getValue(), rulefile.getValue());

      return Results.success("Lint Results: \n" + result.toString());
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(dockerfile).add(rulefile);
   }
}