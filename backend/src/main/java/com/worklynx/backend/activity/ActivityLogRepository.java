package com.worklynx.backend.activity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

  List<ActivityLog> findByOrganizationIdOrderByCreatedAtDesc(Long orgId);

  List<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
