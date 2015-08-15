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
   public abstract List<Container> listContainers(DockerClient dc);

   public abstract List<Container> listContainers(DockerClient dc, boolean showSize, boolean showAll, int limit,
            String sinceId,
            String beforeID, Filters filters);

   public abstract CreateContainerCmd createContainerCMD(DockerClient dc, String image);

   public abstract void startContainer(DockerClient dc, String containerId);

   public abstract ExecCreateCmdResponse execCreate(DockerClient dc, String containerId);

   public abstract ExecCreateCmdResponse execCreate(DockerClient dc, String containerId, String[] cmd,
            boolean attachStdin,
            boolean attachStdout, boolean attachStderr, boolean tty);

   public abstract InspectContainerResponse inspectContainer(DockerClient dc, String containerId);

   public abstract void removeContainer(DockerClient dc, String containerId);

   public abstract void removeContainer(DockerClient dc, String containerId, boolean removeVolumes, boolean force);

   public abstract Integer waitContainer(DockerClient dc, String containerId);

   public abstract Frame attachContainer(DockerClient dc, String containerId) throws Exception;

   public abstract Frame attachContainer(DockerClient dc, String containerId, boolean logs,
            boolean followStream, boolean timestamps, boolean stdout, boolean stderr) throws Exception;

   public abstract InputStream execStart(DockerClient dc, String containerId);

   public abstract InputStream execStart(DockerClient dc, String containerId, boolean detach, boolean tty);

   public abstract InspectExecResponse inspectExec(DockerClient dc, String execId);

   public abstract Frame logContainer(DockerClient dc, String containerId) throws Exception;

   public abstract Frame logContainer(DockerClient dc, String containerId, boolean followStream, boolean timestamps,
            boolean stdout, boolean stderr, int tail) throws Exception;

   public abstract InputStream copyFileFromContainer(DockerClient dc, String containerId, String resource);

   public abstract InputStream copyFileFromContainer(DockerClient dc, String containerId, String resource,
            String hostPath);

   public abstract List<ChangeLog> containerDiff(DockerClient dc, String containerId);

   public abstract void stopContainer(DockerClient dc, String containerId);

   public abstract void stopContainer(DockerClient dc, String containerId, int timeout);

   public abstract void killContainer(DockerClient dc, String containerId);

   public abstract void killContainer(DockerClient dc, String containerId, String signal);

   public abstract void restartContainer(DockerClient dc, String containerId);

   public abstract void restartContainer(DockerClient dc, String containerId, int timeout);

   public abstract BuildResponseItem buildImage(DockerClient dc, String tag, boolean noCache, boolean remove,
            boolean quiet,
            boolean pull, File dockerFile, File baseDirectory, InputStream tarInputStream,
            AuthConfigurations buildAuthConfigs) throws Exception;

   public abstract BuildResponseItem buildImage(DockerClient dc, File dockerFileOrFolder) throws Exception;

   public abstract BuildResponseItem buildImage(DockerClient dc, File dockerFileOrFolder, String tag, boolean noCache,
            boolean remove, boolean quiet,
            boolean pull, File baseDirectory, InputStream tarInputStream,
            AuthConfigurations buildAuthConfigs) throws Exception;

   public abstract BuildResponseItem buildImage(DockerClient dc, InputStream tarInputStream) throws Exception;

   public abstract BuildResponseItem buildImage(DockerClient dc, InputStream tarInputStream, String tag,
            boolean noCache,
            boolean remove, boolean quiet,
            boolean pull, File dockerFile, File baseDirectory,
            AuthConfigurations buildAuthConfigs) throws Exception;

   public abstract TopContainerResponse topContainer(DockerClient dc, String containerId);

   public abstract TopContainerResponse topContainer(DockerClient dc, String containerId, String psArgs);

   public abstract void tagImage(DockerClient dc, String imageId, String repository, String tag);

   public abstract void tagImage(DockerClient dc, String imageId, String repository, String tag, boolean force);

   public abstract void pauseContainer(DockerClient dc, String containerId);

   public abstract void unpauseContainer(DockerClient dc, String containerId);

   public abstract Event events(DockerClient dc) throws InterruptedException;

   public abstract Event events(DockerClient dc, String since, String until, Filters filters) throws InterruptedException;

   public abstract Statistics statsCmd(DockerClient dc, String containerId) throws InterruptedException, IOException;

}