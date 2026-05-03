package com.worklynx.backend.subscription;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UsageLimitService {

  private final Map<LimitType, Map<Subscription.Plan, Integer>> limits = new HashMap<>();

  public UsageLimitService() {

    // PROJECTS
    limits.put(LimitType.PROJECTS_PER_ORG, Map.of(
        Subscription.Plan.FREE, 1,
        Subscription.Plan.PRO, 50,
        Subscription.Plan.ENTERPRISE, Integer.MAX_VALUE));

    // MEMBERS
    limits.put(LimitType.MEMBERS_PER_ORG, Map.of(
        Subscription.Plan.FREE, 5,
        Subscription.Plan.PRO, 50,
        Subscription.Plan.ENTERPRISE, Integer.MAX_VALUE));

    // TASKS
    limits.put(LimitType.TASKS_PER_ORG, Map.of(
        Subscription.Plan.FREE, 50,
        Subscription.Plan.PRO, 10000,
        Subscription.Plan.ENTERPRISE, Integer.MAX_VALUE));
  }

  public int getLimit(LimitType type, Subscription.Plan plan) {
    return limits.get(type).get(plan);
  }
}
