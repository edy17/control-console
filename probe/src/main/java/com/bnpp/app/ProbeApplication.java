package com.bnpp.app;

import com.bnpp.app.application.service.ClusterService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;

@QuarkusMain
public class ProbeApplication implements QuarkusApplication {

  @Inject
  ClusterService clusterService;

  @Override
  public int run(String... args) throws InterruptedException {
    clusterService.waitAggregatorConnection();
    Quarkus.waitForExit();
    return 0;
  }
}
