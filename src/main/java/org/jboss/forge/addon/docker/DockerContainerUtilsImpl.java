package org.jboss.forge.addon.docker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.AttachContainerCmd;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.CommitCmd;
import com.github.dockerjava.api.command.ContainerDiffCmd;
import com.github.dockerjava.api.command.CopyFileFromContainerCmd;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.EventsCmd;
import com.github.dockerjava.api.command.ExecCreateCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectExecCmd;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.command.KillContainerCmd;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.command.PauseContainerCmd;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.RestartContainerCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.TagImageCmd;
import com.github.dockerjava.api.command.TopContainerCmd;
import com.github.dockerjava.api.command.TopContainerResponse;
import com.github.dockerjava.api.command.UnpauseContainerCmd;
import com.github.dockerjava.api.command.WaitContainerCmd;
import com.github.dockerjava.api.model.AuthConfigurations;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.ChangeLog;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.Filters;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.async.ResultCallbackTemplate;

public class DockerContainerUtilsImpl implements DockerContainerUtils
{

   @Override
   public List<Container> listContainers(final DockerClient dockerClient)
   {
      return listContainers(dockerClient, false, false, 1000, null, null, null);
   }

   @Override
   public List<Container> listContainers(final DockerClient dockerClient, boolean showSize, boolean showAll, int limit,
            String sinceId,
            String beforeID, Filters filters)
   {
      ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
      if (beforeID != null)
         listContainersCmd.withBefore(beforeID);
      if (filters != null)
         listContainersCmd.withFilters(filters);
      listContainersCmd.withLimit(limit);
      listContainersCmd.withShowAll(showAll);
      listContainersCmd.withShowSize(showSize);
      if (sinceId != null)
         listContainersCmd.withSince(sinceId);
      List<Container> exec = listContainersCmd.exec();
      return exec;
   }

   @Override
   public CreateContainerCmd createContainerCMD(final DockerClient dockerClient, String image)
   {
      CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(image);
      return createContainerCmd;
   }

   @Override
   public void startContainer(final DockerClient dockerClient, String containerId)
   {
      StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(containerId);
      startContainerCmd.withContainerId(containerId);
      startContainerCmd.exec();
   }

   @Override
   public ExecCreateCmdResponse execCreate(final DockerClient dockerClient, String containerId)
   {
      return execCreate(dockerClient, containerId, null, false, false, false, false);
   }

   @Override
   public ExecCreateCmdResponse execCreate(final DockerClient dockerClient, String containerId, String[] cmd,
            boolean attachStdin,
            boolean attachStdout, boolean attachStderr, boolean tty)
   {
      ExecCreateCmd execCreateCmd = dockerClient.execCreateCmd(containerId);
      execCreateCmd.withAttachStderr(attachStderr);
      execCreateCmd.withAttachStdin(attachStdin);
      execCreateCmd.withAttachStdout(attachStdout);
      if (cmd != null)
         execCreateCmd.withCmd(cmd);
      execCreateCmd.withContainerId(containerId);
      execCreateCmd.withTty(tty);
      ExecCreateCmdResponse exec = execCreateCmd.exec();
      return exec;
   }

   @Override
   public InspectContainerResponse inspectContainer(final DockerClient dockerClient, String containerId)
   {
      InspectContainerCmd inspectContainerCmd = dockerClient.inspectContainerCmd(containerId);
      inspectContainerCmd.withContainerId(containerId);
      InspectContainerResponse exec = inspectContainerCmd.exec();
      return exec;
   }

   @Override
   public void removeContainer(final DockerClient dockerClient, String containerId)
   {
      removeContainer(dockerClient, containerId, false, false);
   }

   @Override
   public void removeContainer(final DockerClient dockerClient, String containerId, boolean removeVolumes, boolean force)
   {
      RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(containerId);
      removeContainerCmd.withContainerId(containerId);
      removeContainerCmd.withForce(force);
      removeContainerCmd.withRemoveVolumes(removeVolumes);
      removeContainerCmd.exec();
   }

   @Override
   public Integer waitContainer(final DockerClient dockerClient, String containerId)
   {
      WaitContainerCmd waitContainerCmd = dockerClient.waitContainerCmd(containerId);
      waitContainerCmd.withContainerId(containerId);
      Integer exec = waitContainerCmd.exec();
      return exec;
   }

   @Override
   public Frame attachContainer(final DockerClient dockerClient, String containerId) throws Exception
   {
      return attachContainer(dockerClient, containerId, false, false, false, false, false);
   }

   @Override
   public Frame attachContainer(final DockerClient dockerClient, String containerId, boolean logs,
            boolean followStream, boolean timestamps, boolean stdout, boolean stderr) throws Exception
   {
      AttachContainerCmd attachContainerCmd = dockerClient.attachContainerCmd(containerId);
      attachContainerCmd.withContainerId(containerId);
      attachContainerCmd.withFollowStream(followStream);
      attachContainerCmd.withLogs(logs);
      attachContainerCmd.withStdErr(stderr);
      attachContainerCmd.withStdOut(stdout);
      attachContainerCmd.withTimestamps(timestamps);

      CollectFramesCallback collectFramesCallback = new CollectFramesCallback()
      {
         @Override
         public void onNext(Frame frame)
         {
            super.onNext(frame);
         };
      };

      attachContainerCmd.exec(collectFramesCallback);

      collectFramesCallback.awaitCompletion(30, TimeUnit.SECONDS);

      collectFramesCallback.close();

      return collectFramesCallback.awaitFrame();

   }

   @Override
   public InputStream execStart(final DockerClient dockerClient, String containerId)
   {
      return execStart(dockerClient, containerId, false, false);
   }

   @Override
   public InputStream execStart(final DockerClient dockerClient, String containerId, boolean detach, boolean tty)
   {
      ExecStartCmd execStartCmd = dockerClient.execStartCmd(containerId);
      execStartCmd.withDetach(detach);
      execStartCmd.withTty(detach);
      InputStream exec = execStartCmd.exec();
      return exec;
   }

   @Override
   public InspectExecResponse inspectExec(final DockerClient dockerClient, String execId)
   {
      InspectExecCmd inspectExecCmd = dockerClient.inspectExecCmd(execId);
      inspectExecCmd.withExecId(execId);
      InspectExecResponse exec = inspectExecCmd.exec();
      return exec;
   }

   @Override
   public Frame logContainer(final DockerClient dockerClient, String containerId) throws Exception
   {
      return logContainer(dockerClient, containerId, false, false, false, false, 50);

   }

   @Override
   public Frame logContainer(final DockerClient dockerClient, String containerId, boolean followStream,
            boolean timestamps,
            boolean stdout, boolean stderr, int tail) throws Exception
   {

      LogContainerCmd logContainerCmd = dockerClient.logContainerCmd(containerId);
      logContainerCmd.withContainerId(containerId);
      logContainerCmd.withFollowStream(followStream);
      logContainerCmd.withStdErr(stderr);
      logContainerCmd.withStdOut(stdout);
      logContainerCmd.withTail(tail);
      logContainerCmd.withTimestamps(timestamps);

      CollectFramesCallback loggingCallback = new CollectFramesCallback()
      {
         @Override
         public void onNext(Frame frame)
         {
            items.add(frame);

         }

      };
      logContainerCmd.exec(loggingCallback);
      return loggingCallback.awaitFrame();

   }

   public static class CollectFramesCallback extends CollectStreamItemCallback<CollectFramesCallback, Frame>
   {

      public Frame awaitFrame() throws Exception
      {
         awaitCompletion();
         Frame item = null;
         if (items.size() > 0)
            item = items.get(items.size() - 1);
         return item;
      }

   }

   @Override
   public InputStream copyFileFromContainer(final DockerClient dockerClient, String containerId, String resource)
   {
      return copyFileFromContainer(dockerClient, containerId, resource, null);
   }

   @Override
   public InputStream copyFileFromContainer(final DockerClient dockerClient, String containerId, String resource,
            String hostPath)
   {
      CopyFileFromContainerCmd copyFileFromContainerCmd = dockerClient.copyFileFromContainerCmd(containerId, resource);
      copyFileFromContainerCmd.withContainerId(containerId);
      copyFileFromContainerCmd.withResource(resource);
      if (hostPath != null)
         copyFileFromContainerCmd.withHostPath(hostPath);
      InputStream exec = copyFileFromContainerCmd.exec();
      return exec;
   }

   @Override
   public List<ChangeLog> containerDiff(final DockerClient dockerClient, String containerId)
   {
      ContainerDiffCmd containerDiffCmd = dockerClient.containerDiffCmd(containerId);
      containerDiffCmd.withContainerId(containerId);
      List<ChangeLog> exec = containerDiffCmd.exec();
      return exec;
   }

   @Override
   public void stopContainer(final DockerClient dockerClient, String containerId)
   {
      stopContainer(dockerClient, containerId, 10);
   }

   @Override
   public void stopContainer(final DockerClient dockerClient, String containerId, int timeout)
   {
      StopContainerCmd stopContainerCmd = dockerClient.stopContainerCmd(containerId);
      stopContainerCmd.withContainerId(containerId);
      stopContainerCmd.withTimeout(timeout);
      stopContainerCmd.exec();
   }

   @Override
   public void killContainer(final DockerClient dockerClient, String containerId)
   {
      killContainer(dockerClient, containerId, "KILL");
   }

   @Override
   public void killContainer(final DockerClient dockerClient, String containerId, String signal)
   {
      KillContainerCmd killContainerCmd = dockerClient.killContainerCmd(containerId);
      killContainerCmd.withContainerId(containerId);
      if (signal != null)
         killContainerCmd.withSignal(signal);
      killContainerCmd.exec();
   }

   @Override
   public void restartContainer(final DockerClient dockerClient, String containerId)
   {
      restartContainer(dockerClient, containerId, 10);
   }

   @Override
   public void restartContainer(final DockerClient dockerClient, String containerId, int timeout)
   {
      RestartContainerCmd restartContainerCmd = dockerClient.restartContainerCmd(containerId);
      restartContainerCmd.withContainerId(containerId);
      restartContainerCmd.withtTimeout(timeout);
      restartContainerCmd.exec();
   }

   @Override
   public CommitCmd commitCmd(final DockerClient dockerClient, String containerId)
   {
     return  dockerClient.commitCmd(containerId);
      
   }

   @Override
   public BuildResponseItem buildImage(final DockerClient dockerClient, String tag, boolean noCache, boolean remove,
            boolean quiet,
            boolean pull, File dockerFile, File baseDirectory, InputStream tarInputStream,
            AuthConfigurations buildAuthConfigs) throws Exception
   {
      BuildImageCmd buildImageCmd = dockerClient.buildImageCmd();
      buildImageCmd.withBaseDirectory(baseDirectory);
      buildImageCmd.withBuildAuthConfigs(buildAuthConfigs);
      buildImageCmd.withDockerfile(dockerFile);
      buildImageCmd.withNoCache(noCache);
      buildImageCmd.withPull(pull);
      buildImageCmd.withQuiet(quiet);
      buildImageCmd.withRemove(noCache);
      buildImageCmd.withTag(tag);
      buildImageCmd.withTarInputStream(tarInputStream);

      BuildLogCallback resultCallback = new BuildLogCallback();
      buildImageCmd.exec(resultCallback);
      return resultCallback.awaitImageId();

   }

   public static class BuildLogCallback extends CollectStreamItemCallback<BuildLogCallback, BuildResponseItem>
   {
      public BuildResponseItem awaitImageId() throws Exception
      {
         awaitCompletion();
         BuildResponseItem item = items.get(items.size() - 1);
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

   @Override
   public BuildResponseItem buildImage(final DockerClient dockerClient, File dockerFileOrFolder) throws Exception
   {
      return buildImage(dockerClient, dockerFileOrFolder, null, false, true, false, false, null, null);
   }

   @Override
   public BuildResponseItem buildImage(final DockerClient dockerClient, File dockerFileOrFolder, String tag,
            boolean noCache,
            boolean remove, boolean quiet,
            boolean pull, File baseDirectory,
            AuthConfigurations buildAuthConfigs) throws Exception
   {
      BuildImageCmd buildImageCmd = dockerClient.buildImageCmd(dockerFileOrFolder);
      if (baseDirectory != null)
         buildImageCmd.withBaseDirectory(baseDirectory);
      if (buildAuthConfigs != null)
         buildImageCmd.withBuildAuthConfigs(buildAuthConfigs);
      buildImageCmd.withDockerfile(dockerFileOrFolder);
      buildImageCmd.withNoCache(noCache);
      buildImageCmd.withPull(pull);
      buildImageCmd.withQuiet(quiet);
      buildImageCmd.withRemove(noCache);
      if (tag != null)
         buildImageCmd.withTag(tag);

      BuildLogCallback resultCallback = new BuildLogCallback();
      buildImageCmd.exec(resultCallback);
      return resultCallback.awaitImageId();
   }

   @Override
   public BuildResponseItem buildImage(final DockerClient dockerClient, InputStream tarInputStream) throws Exception
   {
      return buildImage(dockerClient, tarInputStream, null, false, true, true, false, null);
   }

   @Override
   public BuildResponseItem buildImage(final DockerClient dockerClient, InputStream tarInputStream, String tag,
            boolean noCache,
            boolean remove, boolean quiet,
            boolean pull,
            AuthConfigurations buildAuthConfigs) throws Exception
   {
      BuildImageCmd buildImageCmd = dockerClient.buildImageCmd(tarInputStream);
      if (buildAuthConfigs != null)
         buildImageCmd.withBuildAuthConfigs(buildAuthConfigs);
      buildImageCmd.withNoCache(noCache);
      buildImageCmd.withPull(pull);
      buildImageCmd.withQuiet(quiet);
      buildImageCmd.withRemove(noCache);
      if (tag != null)
         buildImageCmd.withTag(tag);
      buildImageCmd.withTarInputStream(tarInputStream);

      BuildLogCallback resultCallback = new BuildLogCallback();
      buildImageCmd.exec(resultCallback);
      return resultCallback.awaitImageId();
   }

   @Override
   public TopContainerResponse topContainer(final DockerClient dockerClient, String containerId)
   {
      return topContainer(dockerClient, containerId, null);
   }

   @Override
   public TopContainerResponse topContainer(final DockerClient dockerClient, String containerId, String psArgs)
   {
      TopContainerCmd topContainerCmd = dockerClient.topContainerCmd(containerId);
      topContainerCmd.withContainerId(containerId);
      if (psArgs != null)
         topContainerCmd.withPsArgs(psArgs);
      TopContainerResponse exec = topContainerCmd.exec();
      return exec;
   }

   @Override
   public void tagImage(final DockerClient dockerClient, String imageId, String repository, String tag)
   {
      tagImage(dockerClient, imageId, repository, tag, false);
   }

   @Override
   public void tagImage(final DockerClient dockerClient, String imageId, String repository, String tag, boolean force)
   {
      TagImageCmd tagImageCmd = dockerClient.tagImageCmd(imageId, repository, tag);
      tagImageCmd.withForce(force);
      tagImageCmd.withImageId(imageId);
      tagImageCmd.withRepository(repository);
      tagImageCmd.withTag(tag);
      tagImageCmd.exec();
   }

   @Override
   public void pauseContainer(final DockerClient dockerClient, String containerId)
   {
      PauseContainerCmd pauseContainerCmd = dockerClient.pauseContainerCmd(containerId);
      pauseContainerCmd.withContainerId(containerId);
      pauseContainerCmd.exec();
   }

   @Override
   public void unpauseContainer(final DockerClient dockerClient, String containerId)
   {
      UnpauseContainerCmd unpauseContainerCmd = dockerClient.unpauseContainerCmd(containerId);
      unpauseContainerCmd.withContainerId(containerId);
      unpauseContainerCmd.exec();
   }

   @Override
   public Event events(final DockerClient dockerClient) throws InterruptedException
   {
      return events(dockerClient, null, null, null);
   }

   @Override
   public Event events(final DockerClient dockerClient, String since, String until, Filters filters)
            throws InterruptedException
   {
      EventsCmd eventsCmd = dockerClient.eventsCmd();
      if (filters != null)
         eventsCmd.withFilters(filters);
      if (since != null)
         eventsCmd.withSince(since);
      if (until != null)
         eventsCmd.withUntil(until);

      CountDownLatch countDownLatch = new CountDownLatch(10);
      EventCallback eventCallback = new EventCallback(countDownLatch);

      eventsCmd.exec(eventCallback);
      return eventCallback.awaitEvent();
   }

   private class EventCallback extends ResultCallbackTemplate<EventCallback, Event>
   {

      private final CountDownLatch countDownLatch;

      private final List<Event> events = new ArrayList<Event>();

      public EventCallback(CountDownLatch countDownLatch)
      {
         this.countDownLatch = countDownLatch;
      }

      public void onNext(Event event)
      {
         countDownLatch.countDown();
         events.add(event);
      }

      public Event awaitEvent() throws InterruptedException
      {
         awaitCompletion();
         Event event = events.get(events.size() - 1);
         return event;
      }

   }

   @Override
   public Statistics stats(final DockerClient dockerClient, String containerId) throws InterruptedException,
            IOException
   {
      TimeUnit.SECONDS.sleep(1);

      CountDownLatch countDownLatch = new CountDownLatch(5);

      StatsCmd statsCmd = dockerClient.statsCmd();
      statsCmd.withContainerId(containerId);

      StatsCallback statsCallback = statsCmd.exec(new StatsCallback(countDownLatch));
      countDownLatch.await(3, TimeUnit.SECONDS);

      statsCallback.close();

      return statsCallback.awaitStatistics();
   }

   private class StatsCallback extends ResultCallbackTemplate<StatsCallback, Statistics>
   {
      private final CountDownLatch countDownLatch;

      private final List<Statistics> statistics = new ArrayList<Statistics>();

      public StatsCallback(CountDownLatch countDownLatch)
      {
         this.countDownLatch = countDownLatch;
      }

      @Override
      public void onNext(Statistics stats)
      {
         if (stats != null)
         {
            statistics.add(stats);
         }
         countDownLatch.countDown();
      }

      public Statistics awaitStatistics() throws InterruptedException
      {
         awaitCompletion();
         Statistics statistic = statistics.get(statistics.size() - 1);
         return statistic;
      }

   }
}
