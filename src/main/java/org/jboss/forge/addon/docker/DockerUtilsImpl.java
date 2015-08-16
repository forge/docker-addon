package org.jboss.forge.addon.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig.DockerClientConfigBuilder;
import com.github.dockerjava.core.SSLConfig;

public class DockerUtilsImpl implements DockerUtils
{

   @Override
   public DockerClient getDockerClient(String uri)
   {
      DockerClientConfigBuilder createDefaultConfigBuilder = DockerClientConfig.createDefaultConfigBuilder();

      if (uri != null)
         createDefaultConfigBuilder.withUri(uri);

      DockerClientConfig config = createDefaultConfigBuilder.build();

      DockerClient docker = DockerClientBuilder.getInstance(config).build();

      return docker;

   }

   @Override
   public DockerClient getDockerClient(String uri, String version, String username, String password, String email,
            String serverAddress,
            String dockerCfgPath, SSLConfig sslConfig)
   {

      DockerClientConfigBuilder createDefaultConfigBuilder = DockerClientConfig.createDefaultConfigBuilder();

      if (uri != null)
         createDefaultConfigBuilder.withUri(uri);
      if (version != null)
         createDefaultConfigBuilder.withVersion(version);
      if (username != null)
         createDefaultConfigBuilder.withUsername(username);
      if (password != null)
         createDefaultConfigBuilder.withPassword(password);
      if (email != null)
         createDefaultConfigBuilder.withEmail(email);
      if (serverAddress != null)
         createDefaultConfigBuilder.withServerAddress(serverAddress);
      if (dockerCfgPath != null)
         createDefaultConfigBuilder.withDockerCfgPath(dockerCfgPath);
      if (sslConfig != null)
         createDefaultConfigBuilder.withSSLConfig(sslConfig);

      DockerClientConfig config = createDefaultConfigBuilder.build();

      DockerClient docker = DockerClientBuilder.getInstance(config).build();

      return docker;

   }

}
