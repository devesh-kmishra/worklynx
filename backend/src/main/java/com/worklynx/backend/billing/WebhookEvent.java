package com.worklynx.backend.billing;

import java.time.Instant;

import com.worklynx.backend.billing.enums.PaymentProvider;
import com.worklynx.backend.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "webhook_events", indexes = {
    @Index(name = "idx_webhook_provider", columnList = "provider")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_webhook_provider_event", columnNames = { "provider", "provider_event_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEvent extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentProvider provider;

  @Column(name = "provider_event_id", nullable = false)
  private String providerEventId;

  @Column(nullable = false)
  private boolean processed;

  private Instant processedAt;
}
