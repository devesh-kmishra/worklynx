package com.worklynx.backend.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByOrganizationId(Long organizationId);

  List<Task> findByProjectId(Long projectId);

  List<Task> findByAssignedToId(Long userId);

  List<Task> findByCreatedByAndOrganizationIsNull(Long userId);
}
