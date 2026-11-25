package com.worklynx.backend.billing;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worklynx.backend.billing.enums.PaymentProvider;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {

  Optional<WebhookEvent> findByProviderAndProviderEventId(
      PaymentProvider provider,
      String providerEventId);

  boolean existsByProviderAndProviderEventId(
      PaymentProvider provider,
      String providerEventId);
}
