package com.bnpp.app.domain.model;

import com.bnpp.app.utils.CustomDateDeserializer;
import com.bnpp.app.utils.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDateTime;

@RegisterForReflection
public class NodeCondition {

  @JsonSerialize(using = CustomDateSerializer.class)
  @JsonDeserialize(using = CustomDateDeserializer.class)
  private LocalDateTime lastHeartbeatTime;
  @JsonSerialize(using = CustomDateSerializer.class)
  @JsonDeserialize(using = CustomDateDeserializer.class)
  private LocalDateTime lastTransitionTime;
  private String message;
  private String reason;
  private String status;
  private String type;

  public NodeCondition() {
  }

  public NodeCondition(LocalDateTime lastHeartbeatTime, LocalDateTime lastTransitionTime, String message, String reason,
      String status, String type) {
    this.lastHeartbeatTime = lastHeartbeatTime;
    this.lastTransitionTime = lastTransitionTime;
    this.message = message;
    this.reason = reason;
    this.status = status;
    this.type = type;
  }

  public LocalDateTime getLastHeartbeatTime() {
    return lastHeartbeatTime;
  }

  public void setLastHeartbeatTime(LocalDateTime lastHeartbeatTime) {
    this.lastHeartbeatTime = lastHeartbeatTime;
  }

  public LocalDateTime getLastTransitionTime() {
    return lastTransitionTime;
  }

  public void setLastTransitionTime(LocalDateTime lastTransitionTime) {
    this.lastTransitionTime = lastTransitionTime;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "NodeCondition{" +
        "lastHeartbeatTime='" + lastHeartbeatTime + '\'' +
        ", lastTransitionTime='" + lastTransitionTime + '\'' +
        ", message='" + message + '\'' +
        ", reason='" + reason + '\'' +
        ", status='" + status + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}
