package com.worklynx.backend.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

  List<Task> findByOrganizationId(Long orgId);

  List<Task> findByProjectId(Long projectId);

  List<Task> findByAssignedToId(Long userId);

  List<Task> findByCreatedByAndOrganizationIsNull(Long userId);

  long countByOrganizationId(Long orgId);
}
