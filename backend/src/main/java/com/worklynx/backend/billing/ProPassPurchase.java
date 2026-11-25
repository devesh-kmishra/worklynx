package com.worklynx.backend.billing;

import java.time.Instant;

import com.worklynx.backend.common.BaseEntity;
import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "pro_pass_purchases", indexes = {
    @Index(name = "idx_pro_pass_org", columnList = "organization_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_pro_pass_user", columnNames = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProPassPurchase extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @Column(nullable = false)
  private Instant startsAt;

  @Column(nullable = false)
  private Instant expiresAt;

  @Column(nullable = false)
  private Long amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Column(nullable = false)
  private boolean paid;
}
