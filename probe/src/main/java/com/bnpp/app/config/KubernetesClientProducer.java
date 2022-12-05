package com.bnpp.app.config;

import io.fabric8.kubernetes.api.model.Context;
import io.fabric8.kubernetes.api.model.NamedContext;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class KubernetesClientProducer {

  @ConfigProperty(name = "k8s.api.server.url")
  String apiServerUrl;
  @ConfigProperty(name = "k8s.api.server.token")
  String apiServerTokenPath;
  @ConfigProperty(name = "k8s.api.server.ca")
  String apiServerCAPath;
  @ConfigProperty(name = "k8s.api.server.namespace")
  String apiServerNamespacePath;
  @ConfigProperty(name = "k8s.api.cluster.name")
  String clusterName;
  @ConfigProperty(name = "k8s.api.context.name")
  String contextName;
  @ConfigProperty(name = "k8s.api.user.name")
  String userName;

  @Produces
  public KubernetesClient kubernetesClient() throws IOException {
    Path apiServerTokenPathObject = Path.of(apiServerTokenPath);
    String oauthToken = Files.readString(apiServerTokenPathObject);

    Path apiServerNamespacePathObject = Path.of(apiServerNamespacePath);
    String namespace = Files.readString(apiServerNamespacePathObject);

    System.setProperty("kubernetes.disable.autoConfig", "true");
    System.setProperty("kubernetes.auth.tryKubeConfig", "false");
    System.setProperty("kubernetes.auth.tryServiceAccount", "false");
    System.setProperty("kubernetes.tryNamespacePath", "false");

    Config config = new ConfigBuilder()
        .withMasterUrl(apiServerUrl)
        .withCaCertFile(apiServerCAPath)
        .withOauthToken(oauthToken)
        .withNamespace(namespace)
        .withCurrentContext(new NamedContext(
            new Context(clusterName, null, namespace, userName),
            contextName))
        .build();
    return new KubernetesClientBuilder().withConfig(config).build();
  }
}