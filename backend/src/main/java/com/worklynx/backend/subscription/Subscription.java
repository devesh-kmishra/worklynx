package com.worklynx.backend.subscription;

import java.time.Instant;

import com.worklynx.backend.common.BaseEntity;
import com.worklynx.backend.organization.Organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscriptions", indexes = {
    @Index(name = "idx_subscription_org", columnList = "organization_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Plan plan;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  private Instant currentPeriodStart;

  private Instant currentPeriodEnd;

  private String providerSubscriptionId;

  public enum Plan {
    FREE,
    PRO,
    ENTERPRISE
  }

  public enum Status {
    ACTIVE,
    TRIALING,
    PAST_DUE,
    CANCELLED
  }
}
