package com.worklynx.backend.subscription;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

  Optional<Subscription> findByOrganizationId(Long organizationId);
}
