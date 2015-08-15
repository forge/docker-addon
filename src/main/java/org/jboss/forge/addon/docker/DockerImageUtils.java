package org.jboss.forge.addon.docker;

import java.io.InputStream;
import java.util.List;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateImageResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.api.model.SearchItem;

public interface DockerImageUtils
{

   public abstract PullResponseItem pullImage(DockerClient dc, String repository) throws Exception;

   public abstract PullResponseItem pullImage(DockerClient dc, String repository, String tag, String registry,
            AuthConfig authConfig) throws Exception;

   public abstract PushResponseItem pushImage(DockerClient dc, String name) throws Exception;

   public abstract PushResponseItem pushImage(DockerClient dc, String name, String tag, AuthConfig authConfig)
            throws Exception;

   public abstract CreateImageResponse createImage(DockerClient dc, String repository, InputStream imageStream);

   public abstract CreateImageResponse createImage(DockerClient dc, String repository, String tag,
            InputStream imageStream);

   public abstract List<SearchItem> searchImages(DockerClient dc, String term);

   public abstract void removeImage(DockerClient dc, String imageId);

   public abstract void removeImage(DockerClient dc, String imageId, boolean force, boolean noPrune);

   public abstract List<Image> listImages(DockerClient dc);

   public abstract List<Image> listImages(DockerClient dc, boolean showAll, String filters);

   public abstract InspectImageResponse inspectImage(DockerClient dc, String imageId);

   public abstract InputStream saveImage(DockerClient dc, String name);

   public abstract InputStream saveImage(DockerClient dc, String name, String tag);

}