package com.worklynx.backend.project;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  Optional<Project> findByIdAndOrganizationId(Long id, Long organizationId);

  List<Project> findByOrganizationId(Long organizationId);

  boolean existsByIdAndOrganizationId(Long id, Long organizationId);
}
