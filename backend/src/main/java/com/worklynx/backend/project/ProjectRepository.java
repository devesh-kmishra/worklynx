package com.worklynx.backend.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  List<Project> findByOrganizationId(Long organizationId);

  boolean existsByIdAndOrganizationId(Long id, Long organizationId);
}
