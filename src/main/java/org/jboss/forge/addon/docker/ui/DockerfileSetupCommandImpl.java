package org.jboss.forge.addon.docker.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.docker.facets.DockerFacet;
import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.building.ProjectBuilder;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.shrinkwrap.descriptor.api.docker.DockerDescriptor;
import org.jboss.shrinkwrap.descriptor.api.docker.instruction.AddInstruction;

public class DockerfileSetupCommandImpl extends AbstractProjectCommand implements DockerfileCreateCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Dockerfile: Setup")
               .description("Create Dockerfile content for your Project")
               .category(Categories.create("Docker"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      final PackagingFacet facet = getSelectedProject(context.getUIContext()).getFacet(PackagingFacet.class);
      ProjectBuilder builder = facet.createBuilder();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();

      builder.addArguments("clean").addArguments("package").runTests(false)
               .build(new PrintStream(out, true), new PrintStream(err, true));

      Resource<?> finalArtifact = facet.getFinalArtifact();

      if (err.size() == 0 && out.toString().contains("BUILD SUCCESS")
               && !finalArtifact.getFullyQualifiedName().contains("${project.basedir}"))
      {
         DockerFacet df = getSelectedProject(context.getUIContext()).getFacet(DockerFacet.class);
         DockerFileResource dfr = df.getDockerfileResource();
         DockerDescriptor dd = dfr.getDockerDescriptor();

         dd.from("jboss/wildfly");
         boolean flag = false;
         List<AddInstruction> addIns = dd.getAllAdd();
         for (AddInstruction instruction : addIns)
            if (instruction.getSource().equals("COPY target/" + finalArtifact.getName())
                     && instruction.getDestination().equals("/opt/wildfly/standalone/deployments/"))
               flag = true;

         if (!flag)
         {
            dd.add("target/" + finalArtifact.getName(), "/opt/wildfly/standalone/deployments/");
         }

         dfr.setContents(dd);

         return Results.success("Done! Dockerfile content created successfully.");
      }

      else
         return Results.fail("BUILD FAILED:Dockerfile setup cannot proceed.");

   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context)
               && getSelectedProject(context).getRoot().getChild("Dockerfile").exists()
               && getSelectedProject(context).hasFacet(PackagingFacet.class);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}