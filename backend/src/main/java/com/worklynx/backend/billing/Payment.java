package com.worklynx.backend.billing;

import java.time.Instant;

import com.worklynx.backend.billing.enums.PaymentProvider;
import com.worklynx.backend.billing.enums.PaymentStatus;
import com.worklynx.backend.common.BaseEntity;
import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.subscription.Subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_org", columnList = "organization_id"),
    @Index(name = "idx_payment_subscription", columnList = "subscription_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_payment_provider_payment_id", columnNames = { "provider", "provider_payment_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subscription_id")
  private Subscription subscription;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentProvider provider;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  @Column(nullable = false)
  private Long amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Column(name = "provider_order_id")
  private String providerOrderId;

  @Column(name = "provider_payment_id")
  private String providerPaymentId;

  @Column(name = "provider_subscription_id")
  private String providerSubscriptionId;

  private Instant paidAt;
}
