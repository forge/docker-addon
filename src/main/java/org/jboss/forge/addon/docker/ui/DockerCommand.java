package org.jboss.forge.addon.docker.ui;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Streams;

public class DockerCommand extends AbstractUICommand
{

   @Inject
   private UIInputMany<String> arguments;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("docker").description("Docker Commands")
               .category(Categories.create("Docker"));

   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {

      UISelection<Resource<?>> initialSelection = context.getUIContext().getInitialSelection();
      Resource<?> directory = initialSelection.get();
      FileResource<?> fr = (FileResource<?>) directory.reify(FileResource.class);
      File f = fr.getUnderlyingResourceObject();

      UIContext uiContext = context.getUIContext();
      final UIOutput output = uiContext.getProvider().getOutput();

      Result result = null;
      StringBuilder sb = new StringBuilder();

      Iterable<String> value = arguments.getValue();
      if (value != null)
      {
         for (String val : value)
         {
            sb.append(val).append(' ');
         }
      }

      String command = "docker " + sb.toString();

      final Process process = Runtime.getRuntime().exec(command, null, f);

      ExecutorService executor = Executors.newFixedThreadPool(2);

      executor.submit(new Runnable()
      {
         @Override
         public void run()
         {
            Streams.write(process.getInputStream(), output.out());
         }
      });

      executor.submit(new Runnable()
      {
         @Override
         public void run()
         {
            Streams.write(process.getErrorStream(), output.err());
         }
      });

      executor.shutdown();

      int returnCode = process.waitFor();

      if (returnCode == 0)
      {
         result = Results.success();
      }
      else
      {
         result = Results.fail("Error while executing docker command.");
      }
      return result;
   }

}