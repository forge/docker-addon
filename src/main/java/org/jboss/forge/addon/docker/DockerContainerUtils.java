package org.jboss.forge.addon.docker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.command.TopContainerResponse;
import com.github.dockerjava.api.model.AuthConfigurations;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.ChangeLog;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.Filters;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Statistics;

public interface DockerContainerUtils
{
   /**
    * 
    * List Docker Containers
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @return List of Docker Containers.
    */
   public abstract List<Container> listContainers(final DockerClient dockerClient);

   /**
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param showAll true or false, Show all containers. Only running containers are shown by default.
    * @param showSize true or false, Show the containers sizes. This is false by default.
    * @param limit Show `limit` last created containers, include non-running ones. There is no limit by default.
    * @param sinceId Show only containers created since Id, include non-running ones.
    * @param beforeId Show only containers created before Id, include non-running ones.
    * @param filters Filter object representing Docker filters.
    * @return List of Docker Containers.
    */
   public abstract List<Container> listContainers(final DockerClient dockerClient, boolean showSize, boolean showAll,
            int limit,
            String sinceId,
            String beforeID, Filters filters);

   /**
    * 
    * Create Containers
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param image Docker Image to create used to create container .
    * @return CreateContainerCmd object representing the create command.
    */
   public abstract CreateContainerCmd createContainerCMD(final DockerClient dockerClient, String image);

   /**
    * 
    * Start Docker Containers
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID to start.
    */
   public abstract void startContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @return ExecCreateCmdResponse
    */
   public abstract ExecCreateCmdResponse execCreate(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Run a command in a running container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @param cmd Commands array.
    * @param attachStdin Attach STDIN
    * @param attachStdout Attach STDOUT
    * @param attachStderr Atttach STDERR
    * @param tty Allocate a pseudo-TTY
    * @return ExecCreateCmdResponse
    */
   public abstract ExecCreateCmdResponse execCreate(final DockerClient dockerClient, String containerId, String[] cmd,
            boolean attachStdin,
            boolean attachStdout, boolean attachStderr, boolean tty);

   /**
    * 
    * Inspect a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @return InspectContainerResponse representing the container info
    */
   public abstract InspectContainerResponse inspectContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Remove a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID
    */
   public abstract void removeContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Remove a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID
    * @param removeVolumes Remove Volumes
    * @param force Use force
    */
   public abstract void removeContainer(final DockerClient dockerClient, String containerId, boolean removeVolumes,
            boolean force);

   /**
    * 
    * Wait a container Block until container stops, then returns its exit code
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID
    * @return Exit Code
    */
   public abstract Integer waitContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Attach to container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID
    * @return Frame object representing a logging frame
    * @throws Exception
    */
   public abstract Frame attachContainer(final DockerClient dockerClient, String containerId) throws Exception;

   /**
    * 
    * Attach to container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID
    * @param logs true or false, includes logs. Defaults to false.
    * @param followStream true or false, return stream. Defaults to false.
    * @param stdout true or false, includes stdout log. Defaults to false.
    * @param stderr true or false, includes stderr log. Defaults to false.
    * @param timestamps true or false, if true, print timestamps for every log line. Defaults to false.
    * @return Frame object representing a logging frame
    * @throws Exception
    */
   public abstract Frame attachContainer(final DockerClient dockerClient, String containerId, boolean logs,
            boolean followStream, boolean timestamps, boolean stdout, boolean stderr) throws Exception;

   /**
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @return InputStream
    */
   public abstract InputStream execStart(final DockerClient dockerClient, String containerId);

   /**
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @param detach
    * @param tty
    * @return InputStream
    */
   public abstract InputStream execStart(final DockerClient dockerClient, String containerId, boolean detach,
            boolean tty);

   /**
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param execId Docker Container ID.
    * @return InspectExecResponse
    */
   public abstract InspectExecResponse inspectExec(final DockerClient dockerClient, String execId);

   /**
    * 
    * Get container logs
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID
    * @return Frame object representing a logging frame
    * @throws Exception
    */
   public abstract Frame logContainer(final DockerClient dockerClient, String containerId) throws Exception;

   /**
    * 
    * Get container logs
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID
    * @param followStream - true or false, return stream. Defaults to false
    * @param timestamps - true or false, if true, print timestamps for every log line. Defaults to false
    * @param stdout - true or false, includes stdout log. Defaults to false
    * @param stderr - true or false, includes stderr log. Defaults to false
    * @param tail - `all` or `<number>`, Output specified number of lines at the end of logs
    * @return Frame object representing a logging frame
    * @throws Exception
    */
   public abstract Frame logContainer(final DockerClient dockerClient, String containerId, boolean followStream,
            boolean timestamps,
            boolean stdout, boolean stderr, int tail) throws Exception;

   /**
    * 
    * Copy files or folders from a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @param resource Resource
    * @return InputStream
    */
   public abstract InputStream copyFileFromContainer(final DockerClient dockerClient, String containerId,
            String resource);

   /**
    * 
    * Copy files or folders from a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @param resource Resource
    * @param hostPath Host path
    * @return InputStream
    */
   public abstract InputStream copyFileFromContainer(final DockerClient dockerClient, String containerId,
            String resource,
            String hostPath);

   /**
    * 
    * Inspect changes on a container's filesystem
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @return List of ChangeLog object representing the diff
    */
   public abstract List<ChangeLog> containerDiff(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Stop a running container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    */
   public abstract void stopContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Stop a running container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @param timeout Timeout in seconds before killing the container.
    */
   public abstract void stopContainer(final DockerClient dockerClient, String containerId, int timeout);

   /**
    * 
    * Kill a running container.
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    */
   public abstract void killContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Kill a running container.
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @param signal Signal to send
    */
   public abstract void killContainer(final DockerClient dockerClient, String containerId, String signal);

   /**
    * 
    * Restart container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    */
   public abstract void restartContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Restart container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @param timeout Timeout in seconds before killing the container
    */
   public abstract void restartContainer(final DockerClient dockerClient, String containerId, int timeout);

   /**
    * 
    * Build a Docker Image
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param tag Repository name (and optionally a tag) to be applied to the resulting image in case of success
    * @param noCache Do not use cache when building the image
    * @param remove Always remove intermediate containers, even after unsuccessful builds
    * @param quiet Suppress the verbose output generated by the containers
    * @param pull Always attempt to pull a newer version of the image
    * @param dockerFile Dockerfile to build
    * @param baseDirectory Base Directory
    * @param tarInputStream InputStream to build
    * @param buildAuthConfigs Author Configuration
    * @return BuildResponseItem represents a build response stream item
    * @throws Exception
    */
   public abstract BuildResponseItem buildImage(final DockerClient dockerClient, String tag, boolean noCache,
            boolean remove,
            boolean quiet,
            boolean pull, File dockerFile, File baseDirectory, InputStream tarInputStream,
            AuthConfigurations buildAuthConfigs) throws Exception;

   /**
    * 
    * Build a Docker Image from a Dockerfile
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param dockerFileOrFolder
    * @return BuildResponseItem represents a build response stream item
    * @throws Exception
    */
   public abstract BuildResponseItem buildImage(final DockerClient dockerClient, File dockerFileOrFolder)
            throws Exception;

   /**
    * 
    * Build a Docker Image from Dockerfile
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param dockerFileOrFolder
    * @param tag Repository name (and optionally a tag) to be applied to the resulting image in case of success
    * @param noCache Do not use cache when building the image
    * @param remove Always remove intermediate containers, even after unsuccessful builds
    * @param quiet Suppress the verbose output generated by the containers
    * @param pull Always attempt to pull a newer version of the image
    * @param baseDirectory Base Dockerfile directory
    * @param buildAuthConfigs Author configuration
    * @return BuildResponseItem represents a build response stream item
    * @throws Exception
    */
   public abstract BuildResponseItem buildImage(final DockerClient dockerClient, File dockerFileOrFolder, String tag,
            boolean noCache,
            boolean remove, boolean quiet,
            boolean pull, File baseDirectory,
            AuthConfigurations buildAuthConfigs) throws Exception;

   /**
    * 
    * Build a Docker image from InputStream
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param tarInputStream
    * @return BuildResponseItem represents a build response stream item
    * @throws Exception
    */
   public abstract BuildResponseItem buildImage(final DockerClient dockerClient, InputStream tarInputStream)
            throws Exception;

   /**
    * 
    * Build a Docker image from InputStream
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param tarInputStream
    * @param tag Repository name (and optionally a tag) to be applied to the resulting image in case of success
    * @param noCache Do not use cache when building the image
    * @param remove Always remove intermediate containers, even after unsuccessful builds
    * @param quiet Suppress the verbose output generated by the containers
    * @param pull Always attempt to pull a newer version of the image
    * @param buildAuthConfigs Author configuration
    * @return BuildResponseItem represents a build response stream item
    * @throws Exception
    */
   public abstract BuildResponseItem buildImage(final DockerClient dockerClient, InputStream tarInputStream,
            String tag,
            boolean noCache,
            boolean remove, boolean quiet,
            boolean pull,
            AuthConfigurations buildAuthConfigs) throws Exception;

   /**
    * 
    * List processes running inside a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @return TopContainerResponse representing results
    */
   public abstract TopContainerResponse topContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * List processes running inside a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @param psArgs
    * @return TopContainerResponse representing results
    */
   public abstract TopContainerResponse topContainer(final DockerClient dockerClient, String containerId, String psArgs);

   /**
    * 
    * Tag an image into a repository
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param image The local image to tag (either a name or an id)
    * @param repository The repository to tag in
    * @param tag The tag string
    */
   public abstract void tagImage(final DockerClient dockerClient, String imageId, String repository, String tag);

   /**
    * 
    * Tag an image into a repository
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param image The local image to tag (either a name or an id)
    * @param repository The repository to tag in
    * @param tag The tag string
    * @param force Use force
    */
   public abstract void tagImage(final DockerClient dockerClient, String imageId, String repository, String tag,
            boolean force);

   /**
    * 
    * Pause a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    */
   public abstract void pauseContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Unpause a container
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    */
   public abstract void unpauseContainer(final DockerClient dockerClient, String containerId);

   /**
    * 
    * Get events
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @return Representation of a Docker event
    * @throws InterruptedException
    */
   public abstract Event events(final DockerClient dockerClient) throws InterruptedException;

   /**
    * 
    * Get events
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param since Show all events created since timestamp
    * @param until Stream events until this timestamp
    * @param filters Filter object representing of Docker filters.
    * @return Representation of a Docker event
    * @throws InterruptedException
    */
   public abstract Event events(final DockerClient dockerClient, String since, String until, Filters filters)
            throws InterruptedException;

   /**
    * 
    * Get container stats
    * 
    * @param dockerClient A docker-java API handle for managing Docker related tasks.
    * @param containerId Docker Container ID.
    * @return Representation of a Docker statistics.
    * @throws InterruptedException
    * @throws IOException
    */
   public abstract Statistics stats(final DockerClient dockerClient, String containerId)
            throws InterruptedException,
            IOException;

}