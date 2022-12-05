package com.bnpp.app.application.service;

import com.bnpp.app.application.task.ClusterTask;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
@ClientEndpoint
public class ClusterService {

  private static final Logger LOG = Logger.getLogger(ClusterService.class);

  private ScheduledExecutorService executor;

  @ConfigProperty(name = "k8s.server.aggregator")
  String aggregatorUrl;
  @Inject
  KubernetesClient kubernetesClient;

  public void waitAggregatorConnection() throws InterruptedException {
    boolean aggregatorIsUp = false;
    while (!aggregatorIsUp) {
      aggregatorIsUp = connectToAggregator();
    }
    LOG.info("Aggregator is connected");
  }

  private boolean connectToAggregator() throws InterruptedException {
    try {
      Session session = ContainerProvider.getWebSocketContainer()
          .connectToServer(ClusterService.class, URI.create(aggregatorUrl + "/probe"));
      return session != null;
    } catch (Exception e) {
      LOG.error("Aggregator is not available");
    }
    TimeUnit.SECONDS.sleep(2);
    return false;
  }

  @OnOpen
  public void open(Session session) {
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleAtFixedRate(new ClusterTask(kubernetesClient, session), 0, 10, TimeUnit.SECONDS);
  }

  @OnClose
  public void onClose(Session session) {
    if (executor != null) {
      executor.shutdown();
    }
  }

  @OnError
  public void onError(Session session, Throwable throwable) throws InterruptedException {
    LOG.error("ClusterService left AggregatorWebSocket on error: " + throwable.getMessage());
    if (executor != null) {
      executor.shutdown();
    }
    waitAggregatorConnection();
  }
}
