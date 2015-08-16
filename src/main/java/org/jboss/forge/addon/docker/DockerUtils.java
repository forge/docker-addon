package org.jboss.forge.addon.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.SSLConfig;

public interface DockerUtils
{
   /**
    * 
    * @param uri The Docker URL, e.g. https://localhost:2376 or unix:///var/run/docker.sock
    * @return A docker-java API handle for managing Docker related tasks.
    */
   public abstract DockerClient getDockerClient(String uri);

   /**
    * 
    * @param uri The Docker URL, e.g. https://localhost:2376 or unix:///var/run/docker.sock
    * @param version The API version, e.g. 1.16.
    * @param username Your registry username (required to push containers).
    * @param password Your registry password.
    * @param email Your registry email.
    * @param serverAddress Your registry's address.
    * @param dockerCfgPath Path to the docker certs.
    * @param sslConfig
    * @return A docker-java API handle for managing Docker related tasks.
    */
   public abstract DockerClient getDockerClient(String uri, String version, String username, String password,
            String email,
            String serverAddress,
            String dockerCfgPath, SSLConfig sslConfig);

}