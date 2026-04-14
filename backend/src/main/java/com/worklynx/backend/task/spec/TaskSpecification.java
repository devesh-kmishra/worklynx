package com.worklynx.backend.task.spec;

import org.springframework.data.jpa.domain.Specification;

import com.worklynx.backend.task.Task;

public class TaskSpecification {

  public static Specification<Task> hasOrganization(Long orgId) {
    return (root, query, cb) -> orgId == null ? cb.isNull(root.get("organization"))
        : cb.equal(root.get("organization").get("id"), orgId);
  }

  public static Specification<Task> hasStatus(String status) {
    return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), Task.Status.valueOf(status));
  }

  public static Specification<Task> assignedTo(Long userId) {
    return (root, query, cb) -> userId == null ? null : cb.equal(root.get("assignedTo").get("id"), userId);
  }

  public static Specification<Task> createdBy(Long userId) {
    return (root, query, cb) -> userId == null ? null : cb.equal(root.get("createdBy").get("id"), userId);
  }
}
