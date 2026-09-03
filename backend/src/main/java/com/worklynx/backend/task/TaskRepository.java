package com.worklynx.backend.task;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

  List<Task> findByOrganizationId(Long orgId);

  List<Task> findByProjectId(Long projectId);

  List<Task> findByAssignedToId(Long userId);

  List<Task> findByCreatedByAndOrganizationIsNull(Long userId);

  @Query("""
      SELECT t
      FROM Task t
      WHERE t.organization IS NULL
        AND t.createdBy.id = :userId
        AND (:status IS NULL OR t.status = :status)
      ORDER BY
        CASE t.priority
          WHEN com.worklynx.backend.task.TaskPriority.HIGH THEN 3
          WHEN com.worklynx.backend.task.TaskPriority.MEDIUM THEN 2
          WHEN com.worklynx.backend.task.TaskPriority.LOW THEN 1
        END DESC,
        t.createdAt DESC
      """)
  Page<Task> findPersonalTasksByPriority(
      @Param("userId") Long userId,
      @Param("status") Task.Status status,
      Pageable pageable);

  long countByOrganizationId(Long orgId);
}
