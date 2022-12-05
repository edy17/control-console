package com.bnpp.app.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Arrays;

@RegisterForReflection
public enum Health {

  HEALTHY("Healthy", "True"),
  UNHEALTHY("Unhealthy", "False"),
  UNKNOWN(null, null);

  private String label;
  private String status;

  Health(String label, String status) {
    this.label = label;
    this.status = status;
  }

  @JsonValue
  public String getLabel() {
    return label;
  }

  @JsonCreator
  public static Health getValueOfLabel(String label) {
    return Arrays.stream(Health.values())
        .filter(health -> label.equalsIgnoreCase(health.getLabel()))
        .findFirst()
        .orElse(UNKNOWN);
  }


  public String getStatus() {
    return status;
  }

  public static Health getValueOfStatus(String status) {
    return Arrays.stream(Health.values())
        .filter(health -> status.equalsIgnoreCase(health.getStatus()))
        .findFirst()
        .orElse(UNKNOWN);
  }
}
