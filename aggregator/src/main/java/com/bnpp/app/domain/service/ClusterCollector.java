package com.bnpp.app.domain.service;

import com.bnpp.app.domain.model.Cluster;
import java.util.Collection;


public interface ClusterCollector {


  Collection<Cluster> getClusters();

  void collect(Cluster cluster);
}
