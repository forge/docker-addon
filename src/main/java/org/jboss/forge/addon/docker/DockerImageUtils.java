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

   /**
    * 
    * Pull an image from repository
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param repository The repository to pull from.
    * @return A pull response stream item
    * @throws Exception No such image was found.
    */
   public abstract PullResponseItem pullImage(final DockerClient dockerClient, String repository) throws Exception;

   /**
    * 
    * Pull an image from repository
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param repository The repository to pull from.
    * @param tag Tag for this image
    * @param registry
    * @param authConfig Author Configuration
    * @return A pull response stream item
    * @throws Exception No such image was found.
    */
   public abstract PullResponseItem pullImage(final DockerClient dockerClient, String repository, String tag,
            String registry,
            AuthConfig authConfig) throws Exception;

   /**
    * 
    * Push an image or a repository to the registry
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param name The name, e.g. "alexec/busybox" or just "busybox" if you want to default. Not null.
    * @return A push response stream item
    * @throws Exception No such image was found.
    */
   public abstract PushResponseItem pushImage(final DockerClient dockerClient, String name) throws Exception;

   /**
    * 
    * Push an image or a repository to the registry
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param name The name, e.g. "alexec/busybox" or just "busybox" if you want to default. Not null.
    * @param tag Tag for this image
    * @param authConfig Author Configuration
    * @return A push response stream item
    * @throws Exception No such image was found.
    */
   public abstract PushResponseItem pushImage(final DockerClient dockerClient, String name, String tag,
            AuthConfig authConfig)
            throws Exception;

   /**
    * 
    * Create an image by importing the given stream of a tar file
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param repository the repository to import to
    * @param imageStream the InputStream of the tar file
    * @return Response after creation
    */
   public abstract CreateImageResponse createImage(final DockerClient dockerClient, String repository,
            InputStream imageStream);

   /**
    * 
    * Create an image by importing the given stream of a tar file.
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param repository the repository to import to
    * @param tag any tag for this image
    * @param imageStream the InputStream of the tar file
    * @return Response after creation
    */
   public abstract CreateImageResponse createImage(final DockerClient dockerClient, String repository, String tag,
            InputStream imageStream);

   /**
    * 
    * Search images
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param term Search term
    * @return List of search items found.
    */
   public abstract List<SearchItem> searchImages(final DockerClient dockerClient, String term);

   /**
    * 
    * Remove images
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param imageId Image ID to remove
    */
   public abstract void removeImage(final DockerClient dockerClient, String imageId);

   /**
    * 
    * Remove images
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param imageId Image ID to remove
    * @param force Force removal of the image
    * @param noPrune Do not delete untagged parents
    */
   public abstract void removeImage(final DockerClient dockerClient, String imageId, boolean force, boolean noPrune);

   /**
    * 
    * List images
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @return List of images
    */
   public abstract List<Image> listImages(final DockerClient dockerClient);

   /**
    * 
    * List images
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param showAll Show all images (by default filter out the intermediate image layers)
    * @param filters Provide filter values (i.e. 'dangling=true')
    * @return List of images
    */
   public abstract List<Image> listImages(final DockerClient dockerClient, boolean showAll, String filters);

   /**
    * 
    * Inspect the details of an image
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param imageId The image ID to inspect
    * @return InspectImageResponse containing image details
    */
   public abstract InspectImageResponse inspectImage(final DockerClient dockerClient, String imageId);

   /**
    * 
    * Save an image to a tar archive
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param name Image name to save
    * @return InputStream
    */
   public abstract InputStream saveImage(final DockerClient dockerClient, String name);

   /**
    * 
    * Save an image to a tar archive
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param name Image name to save
    * @param tag Tag for the image
    * @return InputStream
    */
   public abstract InputStream saveImage(final DockerClient dockerClient, String name, String tag);

}