package com.bnpp.app.web;

import io.fabric8.kubernetes.api.model.ComponentStatus;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/k8s")
public class K8sController {

  @Inject
  KubernetesClient kubernetesClient;

  @GET
  @Path("/nodes")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Node> getNodes() {
    return kubernetesClient.nodes().list().getItems();
  }

  @GET
  @Path("/cs")
  @Produces(MediaType.APPLICATION_JSON)
  public List<ComponentStatus> getCS() {
    return kubernetesClient.componentstatuses().list().getItems();
  }

  @GET
  @Path("/top")
  @Produces(MediaType.APPLICATION_JSON)
  public List<NodeMetrics> getTop() {
    return kubernetesClient.top().nodes().metrics().getItems();
  }
}
