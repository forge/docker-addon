package org.jboss.forge.addon.docker.ui;

/*
 * Command to auto-generate Dockerfile content your project.
 * 
 * It builds and packages your project and then generates the corresponding Dockerfile for deployment to docker containers.
 * 
 * Sample Dockerfile Format created by this command:
 * 
 * FROM jboss/wildfly
 * ADD target/your-awesome-app.war /opt/jboss/wildfly/standalone/deployments/
 * 
 */
public interface DockerfileSetupCommand
{

}
