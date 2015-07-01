package org.jboss.forge.addon.docker.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.docker.facets.DockerFacet;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Implementation of the "dockerfile-setup" command.Installs {@link DockerFacet} for new projects.
 * 
 * @author <a href="mailto:devanshu911@gmail.com">Devanshu Singh</a>
 */
public class DockerfileSetupCommandImpl extends AbstractProjectCommand implements DockerfileSetupCommand
{

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private DockerFacet facet;

   @Inject
   private ProjectFactory projectFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Dockerfile: Setup")
               .description("Prepares the project for functioning in Docker context")
               .category(Categories.create("Docker"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context), facet))
      {
         return Results.success("Dockerfile has been installed.");
      }
      return Results.fail("Could not install Dockerfile.");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context)
               && !getSelectedProject(context).getRoot().getChild("Dockerfile").exists();
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
