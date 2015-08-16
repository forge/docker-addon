package org.jboss.forge.addon.docker.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

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
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * DockerCommand supports running Docker CLI commands on the Forge CLI. Eg: "docker images" shows your Docker images on
 * the Forge CLI just as it works on a usual CLI.
 * 
 * @author Devanshu
 *
 */
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
      File f = new File(fr.getFullyQualifiedName());

      context.getUIContext();
      StringBuilder sb = new StringBuilder();

      Iterable<String> value = arguments.getValue();
      if (value != null)
      {
         for (String val : value)
         {
            sb.append(val).append(' ');
         }
      }

      StringBuffer output = new StringBuffer();
      StringBuffer output1 = new StringBuffer();
      Process p = null;

      String command = "docker " + sb.toString();

      try
      {

         p = Runtime.getRuntime().exec(command, null, f);
         p.waitFor();
         BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
         BufferedReader stderrReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

         String line = "";
         while ((line = reader.readLine()) != null)
            output.append(line + "\n");

         while ((line = stderrReader.readLine()) != null)
            output1.append(line + "\n");

         reader.close();
         stderrReader.close();

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      if (p.exitValue() != 0)
         return Results.fail(output1.toString() + "\nExit Value:" + p.exitValue());

      return Results.success(output.toString());

   }

}
