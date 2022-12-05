package com.bnpp.app.domain.model;

import static com.bnpp.app.domain.model.Health.HEALTHY;
import static com.bnpp.app.utils.JsonUtil.K8S_DATE_TIME_FORMATTER;
import static com.bnpp.app.utils.JsonUtil.ZONE_ID;

import com.bnpp.app.utils.CustomDateDeserializer;
import com.bnpp.app.utils.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ComponentCondition;
import io.fabric8.kubernetes.api.model.ComponentStatus;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@RegisterForReflection
public class Cluster {

  private static final String READY_TYPE = "Ready";

  @JsonSerialize(using = CustomDateSerializer.class)
  @JsonDeserialize(using = CustomDateDeserializer.class)
  private LocalDateTime pushAt;
  private String name;
  private String workers;
  private BigDecimal cpu;
  private BigDecimal memory;
  private Health controllerManager;
  private Health scheduler;
  private String etcd;

  public static Cluster buildCluster(String clusterName, Map<String, ComponentStatus> componentStatuses,
      Map<String, Node> nodes, Collection<NodeMetrics> metrics) {
    final Cluster cluster = new Cluster();
    cluster.setPushAt(LocalDateTime.now(ZONE_ID));
    cluster.setName(clusterName);

    int etcdCount = 0;
    int upEtcdCount = 0;
    for (ComponentStatus cs : componentStatuses.values()) {
      String name = cs.getMetadata().getName();
      String status = Optional.ofNullable(cs.getConditions().get(0)).map(ComponentCondition::getStatus)
          .orElse(null);
      if ("controller-manager".equalsIgnoreCase(name)) {
        cluster.setControllerManager(Health.getValueOfStatus(status));
      } else if ("scheduler".equalsIgnoreCase(name)) {
        cluster.setScheduler(Health.getValueOfStatus(status));
      } else if ((name != null) && (name.startsWith("etcd"))) {
        etcdCount += 1;
        if (Health.getValueOfStatus(status).equals(HEALTHY)) {
          upEtcdCount += 1;
        }
      }
    }
    cluster.setEtcd(upEtcdCount + "/" + etcdCount);

    final int nodeCount = nodes.size();
    int upNodeCount = 0;
    for (Node node : nodes.values()) {
      NodeCondition recentCondition = node.getStatus().getConditions().stream()
          .map(n -> {
            LocalDateTime lastHeartbeatTime = LocalDateTime.parse(n.getLastHeartbeatTime(), K8S_DATE_TIME_FORMATTER);
            LocalDateTime lastTransitionTime = LocalDateTime.parse(n.getLastTransitionTime(), K8S_DATE_TIME_FORMATTER);
            return new NodeCondition(lastHeartbeatTime, lastTransitionTime, n.getMessage(),
                n.getReason(), n.getStatus(), n.getType());
          })
          .filter(nodeCondition -> READY_TYPE.equals(nodeCondition.getType()))
          .max(Comparator.comparing(NodeCondition::getLastHeartbeatTime))
          .orElse(new NodeCondition());
      if ("True".equalsIgnoreCase(recentCondition.getStatus())) {
        upNodeCount += 1;
      }
    }
    cluster.setWorkers(upNodeCount + "/" + nodeCount);

    BigDecimal cpuPercentage = BigDecimal.valueOf(0);
    BigDecimal memoryPercentage = BigDecimal.valueOf(0);
    for (NodeMetrics metric : metrics) {
      Node node = nodes.get(metric.getMetadata().getName());
      Map<String, Quantity> used = metric.getUsage();
      if (Optional.ofNullable(node).map(Node::getStatus).map(NodeStatus::getAllocatable).isPresent() &&
          (used != null)) {
        cpuPercentage = cpuPercentage.add(
            calculateUsage(used.get("cpu"), node.getStatus().getAllocatable().get("cpu")));
        memoryPercentage = memoryPercentage.add(
            calculateUsage(used.get("memory"), node.getStatus().getAllocatable().get("memory")));
      }
    }
    cpuPercentage = cpuPercentage.divide(BigDecimal.valueOf(nodeCount), 2, RoundingMode.FLOOR);
    memoryPercentage = memoryPercentage.divide(BigDecimal.valueOf(nodeCount), 2, RoundingMode.FLOOR);
    cluster.setCpu(cpuPercentage);
    cluster.setMemory(memoryPercentage);

    return cluster;
  }

  private static BigDecimal calculateUsage(Quantity used, Quantity total) {
    return Quantity.getAmountInBytes(used)
        .divide(Quantity.getAmountInBytes(total), 2, RoundingMode.FLOOR)
        .multiply(BigDecimal.valueOf(100));
  }


  public Cluster() {
  }

  public Cluster(LocalDateTime pushAt, String name, String workers, BigDecimal cpu, BigDecimal memory,
      Health controllerManager, Health scheduler, String etcd) {
    this.pushAt = pushAt;
    this.name = name;
    this.workers = workers;
    this.cpu = cpu;
    this.memory = memory;
    this.controllerManager = controllerManager;
    this.scheduler = scheduler;
    this.etcd = etcd;
  }

  public LocalDateTime getPushAt() {
    return pushAt;
  }

  public void setPushAt(LocalDateTime pushAt) {
    this.pushAt = pushAt;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getWorkers() {
    return workers;
  }

  public void setWorkers(String workers) {
    this.workers = workers;
  }

  public BigDecimal getCpu() {
    return cpu;
  }

  public void setCpu(BigDecimal cpu) {
    this.cpu = cpu;
  }

  public BigDecimal getMemory() {
    return memory;
  }

  public void setMemory(BigDecimal memory) {
    this.memory = memory;
  }

  public Health getControllerManager() {
    return controllerManager;
  }

  public void setControllerManager(Health controllerManager) {
    this.controllerManager = controllerManager;
  }

  public Health getScheduler() {
    return scheduler;
  }

  public void setScheduler(Health scheduler) {
    this.scheduler = scheduler;
  }

  public String getEtcd() {
    return etcd;
  }

  public void setEtcd(String etcd) {
    this.etcd = etcd;
  }
}
