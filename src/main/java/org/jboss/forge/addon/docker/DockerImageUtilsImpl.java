package org.jboss.forge.addon.docker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateImageCmd;
import com.github.dockerjava.api.command.CreateImageResponse;
import com.github.dockerjava.api.command.InspectImageCmd;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.command.SaveImageCmd;
import com.github.dockerjava.api.command.SearchImagesCmd;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.async.ResultCallbackTemplate;

public class DockerImageUtilsImpl implements DockerImageUtils
{

   @Override
   public PullResponseItem pullImage(final DockerClient dockerClient, String repository) throws Exception
   {
      return pullImage(dockerClient, repository, null, null, null);
   }

   @Override
   public PullResponseItem pullImage(final DockerClient dockerClient, String repository, String tag, String registry,
            AuthConfig authConfig) throws Exception
   {
      PullImageCmd pullImageCmd = dockerClient.pullImageCmd(repository);
      if (authConfig != null)
         pullImageCmd.withAuthConfig(authConfig);
      if (registry != null)
         pullImageCmd.withRegistry(registry);
      pullImageCmd.withRepository(repository);
      if (tag != null)
         pullImageCmd.withTag(tag);

      PullResponseCallback callback = pullImageCmd.exec(new PullResponseCallback());
      return callback.awaitPullResponse();
   }

   @Override
   public PushResponseItem pushImage(final DockerClient dockerClient, String name) throws Exception
   {
      return pushImage(dockerClient, name, null, null);
   }

   @Override
   public PushResponseItem pushImage(final DockerClient dockerClient, String name, String tag, AuthConfig authConfig) throws Exception
   {
      PushImageCmd pushImageCmd = dockerClient.pushImageCmd(name);
      if (authConfig != null)
         pushImageCmd.withAuthConfig(authConfig);
      pushImageCmd.withName(name);
      if (tag != null)
         pushImageCmd.withTag(tag);

      PushResponseCallback callback = pushImageCmd.exec(new PushResponseCallback());
      return callback.awaitPushResponse();
   }

   @Override
   public CreateImageResponse createImage(final DockerClient dockerClient, String repository, InputStream imageStream)
   {
      return createImage(dockerClient, repository, null, imageStream);
   }

   @Override
   public CreateImageResponse createImage(final DockerClient dockerClient, String repository, String tag, InputStream imageStream)
   {
      CreateImageCmd createImageCmd = dockerClient.createImageCmd(repository, imageStream);
      createImageCmd.withImageStream(imageStream);
      createImageCmd.withRepository(repository);
      if (tag != null)
         createImageCmd.withTag(tag);
      CreateImageResponse exec = createImageCmd.exec();
      return exec;
   }

   @Override
   public List<SearchItem> searchImages(final DockerClient dockerClient, String term)
   {
      SearchImagesCmd searchImagesCmd = dockerClient.searchImagesCmd(term);
      searchImagesCmd.withTerm(term);
      List<SearchItem> exec = searchImagesCmd.exec();
      return exec;
   }

   @Override
   public void removeImage(final DockerClient dockerClient, String imageId)
   {
      removeImage(dockerClient, imageId, false, false);
   }

   @Override
   public void removeImage(final DockerClient dockerClient, String imageId, boolean force, boolean noPrune)
   {
      RemoveImageCmd removeImageCmd = dockerClient.removeImageCmd(imageId);
      removeImageCmd.withForce(force);
      removeImageCmd.withImageId(imageId);
      removeImageCmd.withNoPrune(noPrune);
      removeImageCmd.exec();
   }

   @Override
   public List<Image> listImages(final DockerClient dockerClient)
   {
      return listImages(dockerClient, false, null);
   }

   @Override
   public List<Image> listImages(final DockerClient dockerClient, boolean showAll, String filters)
   {
      ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
      if (filters != null)
         listImagesCmd.withFilters(filters);
      listImagesCmd.withShowAll(showAll);
      List<Image> exec = listImagesCmd.exec();
      return exec;
   }

   @Override
   public InspectImageResponse inspectImage(final DockerClient dockerClient, String imageId)
   {
      InspectImageCmd inspectImageCmd = dockerClient.inspectImageCmd(imageId);
      inspectImageCmd.withImageId(imageId);
      InspectImageResponse exec = inspectImageCmd.exec();
      return exec;
   }

   @Override
   public InputStream saveImage(final DockerClient dockerClient, String name)
   {
      return saveImage(dockerClient, name, null);
   }

   @Override
   public InputStream saveImage(final DockerClient dockerClient, String name, String tag)
   {
      SaveImageCmd saveImageCmd = dockerClient.saveImageCmd(name);
      saveImageCmd.withName(name);
      if (tag != null)
         saveImageCmd.withTag(tag);
      InputStream exec = saveImageCmd.exec();
      return exec;
   }

   public static class PushResponseCallback extends CollectStreamItemCallback<PushResponseCallback, PushResponseItem>
   {
      public PushResponseItem awaitPushResponse() throws Exception
      {
         awaitCompletion();
         PushResponseItem item = items.get(items.size() - 1);
         return item;
      }
   }

   public static class CollectStreamItemCallback<RC_T extends ResultCallback<A_RES_T>, A_RES_T> extends
            ResultCallbackTemplate<RC_T, A_RES_T>
   {
      public final List<A_RES_T> items = new ArrayList<A_RES_T>();

      protected final StringBuffer log = new StringBuffer();

      @Override
      public void onError(Throwable throwable)
      {
         throwable.printStackTrace();
         super.onError(throwable);
      }

      @Override
      public void onNext(A_RES_T item)
      {
         items.add(item);
         log.append("" + item);
      }

      @Override
      public String toString()
      {
         return log.toString();
      }
   }

   public static class PullResponseCallback extends CollectStreamItemCallback<PullResponseCallback, PullResponseItem>
   {
      public PullResponseItem awaitPullResponse() throws Exception
      {
         awaitCompletion();
         PullResponseItem item = items.get(items.size() - 1);
         return item;
      }
   }

}