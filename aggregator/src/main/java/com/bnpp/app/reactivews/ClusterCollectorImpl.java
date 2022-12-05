package com.bnpp.app.reactivews;

import static com.bnpp.app.utils.JsonUtil.DATE_TIME_FORMATTER;
import static com.bnpp.app.utils.JsonUtil.ZONE_ID;

import com.bnpp.app.domain.model.Cluster;
import com.bnpp.app.domain.service.ClusterCollector;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@Path("/collected-clusters")
@ApplicationScoped
public class ClusterCollectorImpl implements ClusterCollector {

  private static final Logger LOG = Logger.getLogger(ClusterCollectorImpl.class);
  private static final Map<String, Cluster> clusterMap = new ConcurrentHashMap<>();
  private static final String SEP = ";";

  @Override
  @GET
  public synchronized List<Cluster> getClusters() {
    LocalDateTime localDateTime = LocalDateTime.now(ZONE_ID).minusSeconds(20);
    return clusterMap.values().stream()
        .filter(cluster -> cluster.getPushAt().isAfter(localDateTime))
        .collect(Collectors.toList());
  }

  @GET
  @Path("/download")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public synchronized Response getClustersInCSV() {
    return Response.ok(createCsv()).build();
  }

  @Override
  @Incoming("incoming-cluster")
  public synchronized void collect(Cluster cluster) {
    clusterMap.put(cluster.getName(), cluster);
  }

  public ByteArrayInputStream createCsv() {
    Collection<Cluster> clusters = clusterMap.values();
    StringBuilder bw = new StringBuilder();
    String header = "pushAt" + SEP +
        "name" + SEP +
        "workers" + SEP +
        "cpu" + SEP +
        "memory" + SEP +
        "controllerManager" + SEP +
        "scheduler" + SEP +
        "etcd" + SEP +
        System.lineSeparator();
    bw.append(header);
    for (Cluster cluster : clusters) {
      String oneLine = formatDate(cluster.getPushAt()) + SEP +
          cluster.getName() + SEP +
          cluster.getWorkers() + SEP +
          cluster.getCpu() + SEP +
          cluster.getMemory() + SEP +
          cluster.getControllerManager() + SEP +
          cluster.getScheduler() + SEP +
          cluster.getEtcd() + SEP +
          System.lineSeparator();
      bw.append(oneLine);
    }
    LOG.info(bw.toString());
    return new ByteArrayInputStream(bw.toString().getBytes(StandardCharsets.ISO_8859_1));
  }

  private String formatDate(LocalDateTime date) {
    if (date != null) {
      return date.format(DATE_TIME_FORMATTER);
    } else {
      return "";
    }
  }
}
