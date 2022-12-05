package com.bnpp.app.application.task;

import com.bnpp.app.domain.model.Cluster;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ComponentStatus;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import org.jboss.logging.Logger;

public class ClusterTask extends TimerTask {

  private static final Logger LOG = Logger.getLogger(ClusterTask.class);

  private final KubernetesClient kubernetesClient;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Map<String, ComponentStatus> componentStatuses = new ConcurrentHashMap<>();
  private final Map<String, Node> nodes = new ConcurrentHashMap<>();
  private final String currentCluster;
  private final Session session;

  public ClusterTask(KubernetesClient kubernetesClient, Session session) {
    this.kubernetesClient = kubernetesClient;
    this.session = session;
    this.currentCluster = URLEncoder.encode(
        kubernetesClient.getConfiguration().getCurrentContext().getContext().getCluster(), StandardCharsets.UTF_8);

    kubernetesClient.nodes().watch(new Watcher<>() {
      @Override
      public void eventReceived(Action action, io.fabric8.kubernetes.api.model.Node node) {
        nodes.put(node.getMetadata().getName(), node);
        sendClusterInfos();
      }

      @Override
      public void onClose(WatcherException cause) {
        LOG.error("Watcher closed due to exception ", cause);
      }
    });
  }

  @Override
  public void run() {
    kubernetesClient.componentstatuses()
        .list()
        .getItems()
        .forEach(cs -> componentStatuses.put(cs.getMetadata().getName(), cs));
    kubernetesClient.nodes()
        .list()
        .getItems()
        .forEach(node -> nodes.put(node.getMetadata().getName(), node));
    sendClusterInfos();
  }

  private void sendClusterInfos() {
    List<NodeMetrics> metrics = kubernetesClient.top().nodes().metrics().getItems();
    Cluster cluster = Cluster.buildCluster(currentCluster, componentStatuses, nodes, metrics);
    try {
      session.getAsyncRemote().sendText(objectMapper.writeValueAsString(cluster));
    } catch (JsonProcessingException e) {
      LOG.error("Error when serializing JSON: ", e);
    }
  }
}
