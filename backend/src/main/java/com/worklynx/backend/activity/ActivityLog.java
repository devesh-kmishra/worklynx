package com.worklynx.backend.activity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activity_logs", indexes = {
    @Index(name = "idx_activity_org", columnList = "organization_id"),
    @Index(name = "idx_activity_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id")
  private Organization organization;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String action; // TASK_CREATED, TASK_UPDATED

  @Column(nullable = false)
  private String entityType; // TASK, PROJECT

  private Long entityId;

  @Column(columnDefinition = "TEXT")
  private String metadata;
}
