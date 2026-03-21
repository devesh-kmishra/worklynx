package com.worklynx.backend.invite;

import java.time.Instant;

import com.worklynx.backend.common.BaseEntity;
import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.organization.OrganizationMember.Role;
import com.worklynx.backend.user.User;

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
@Table(name = "organization_invites", indexes = {
    @Index(name = "idx_invite_token", columnList = "token"),
    @Index(name = "idx_invite_org", columnList = "organization_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationInvite extends BaseEntity {

  @Column(nullable = false)
  private String email;

  @Column(nullable = false, unique = true)
  private String token;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invited_by", nullable = false)
  private User invitedBy;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(nullable = false)
  private Instant expiresAt;

  @Builder.Default
  private boolean accepted = false;
}
