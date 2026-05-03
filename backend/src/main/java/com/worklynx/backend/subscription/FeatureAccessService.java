package com.worklynx.backend.subscription;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class FeatureAccessService {

  private final Map<Feature, Set<Subscription.Plan>> featureMap = new HashMap<>();

  public FeatureAccessService() {

    featureMap.put(Feature.CREATE_PROJECT,
        Set.of(Subscription.Plan.PRO, Subscription.Plan.ENTERPRISE));

    featureMap.put(Feature.INVITE_MEMBERS, Set.of(Subscription.Plan.PRO, Subscription.Plan.ENTERPRISE));

    featureMap.put(Feature.REALTIME_COLLAB, Set.of(Subscription.Plan.PRO, Subscription.Plan.ENTERPRISE));
  }

  public boolean isAllowed(Feature feature, Subscription.Plan plan) {
    return featureMap.getOrDefault(feature, Set.of()).contains(plan);
  }
}
