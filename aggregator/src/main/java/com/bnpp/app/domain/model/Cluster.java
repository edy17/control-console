package com.bnpp.app.domain.model;

import com.bnpp.app.utils.CustomDateDeserializer;
import com.bnpp.app.utils.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RegisterForReflection
public class Cluster {

  @JsonSerialize(using = CustomDateSerializer.class)
  @JsonDeserialize(using = CustomDateDeserializer.class)
  private LocalDateTime pushAt;
  private String name;
  private String workers;
  private BigDecimal cpu;
  private BigDecimal memory;
  private String controllerManager;
  private String scheduler;
  private String etcd;

  public Cluster() {
  }

  public Cluster(LocalDateTime pushAt, String name, String workers, BigDecimal cpu, BigDecimal memory,
      String controllerManager, String scheduler, String etcd) {
    this.pushAt = pushAt;
    this.name = name;
    this.workers = workers;
    this.cpu = cpu;
    this.memory = memory;
    this.controllerManager = controllerManager;
    this.scheduler = scheduler;
    this.etcd = etcd;
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

  public String getControllerManager() {
    return controllerManager;
  }

  public void setControllerManager(String controllerManager) {
    this.controllerManager = controllerManager;
  }

  public String getScheduler() {
    return scheduler;
  }

  public void setScheduler(String scheduler) {
    this.scheduler = scheduler;
  }

  public String getEtcd() {
    return etcd;
  }

  public void setEtcd(String etcd) {
    this.etcd = etcd;
  }

  public LocalDateTime getPushAt() {
    return pushAt;
  }

  public void setPushAt(LocalDateTime pushAt) {
    this.pushAt = pushAt;
  }
}
