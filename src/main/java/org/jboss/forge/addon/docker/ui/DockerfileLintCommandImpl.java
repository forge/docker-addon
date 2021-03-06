package org.jboss.forge.addon.docker.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.docker.linter.DockerfileLineLintResult;
import org.jboss.forge.addon.docker.linter.DockerfileLintResult;
import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Implementation of the "dockerfile-lint" command.Lints Dockerfiles against specified rules written in YAML.
 * 
 * @author <a href="mailto:devanshu911@gmail.com">Devanshu Singh</a>
 */
public class DockerfileLintCommandImpl extends AbstractUICommand implements DockerfileLintCommand
{

   @Inject
   @WithAttributes(label = "Dockerfile", description = "The Dockerfile to be linted", required = true)
   private UISelectOne<DockerFileResource> dockerfile;

   @Inject
   @WithAttributes(label = "Rule File", description = "The rule file against which the dockerfile should be linted")
   private UISelectOne<FileResource<?>> rulefile;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Dockerfile: Lint")
               .description("Dockerfile Linter")
               .category(Categories.create("Docker"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      DockerfileLintResult result;

      UIContext uiContext = context.getUIContext();
      UIOutput output = uiContext.getProvider().getOutput();

      if (!dockerfile.getValue().exists())
         return Results.fail("Dockerfile not found");

      if ((rulefile.getValue() != null) && (!rulefile.getValue().exists()))
         return Results.fail("Rulefile not found");

      if (rulefile.getValue() == null)
         result = dockerfile.getValue().lint();

      else
         result = dockerfile.getValue().lint(rulefile.getValue());

      output.info(output.out(), Integer.toString(result.getInfo()));
      output.warn(output.out(), Integer.toString(result.getWarn()));
      output.error(output.out(), Integer.toString(result.getErrors()));

      for (DockerfileLineLintResult res : result.getLintResults())
      {
         switch (res.getType())
         {
         case INFO:
            output.info(output.out(), res.toString());
            break;
         case ERROR:
            output.error(output.out(), res.toString());
            break;
         case WARN:
            output.warn(output.out(), res.toString());
            break;
         default:
            break;
         }
      }

      return Results.success("Linted Sucessfully");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(dockerfile).add(rulefile);
   }
}