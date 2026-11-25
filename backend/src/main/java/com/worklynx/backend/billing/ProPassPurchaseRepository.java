package com.worklynx.backend.billing;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProPassPurchaseRepository extends JpaRepository<ProPassPurchase, Long> {

  Optional<ProPassPurchase> findByUserId(Long userId);

  boolean existsByUserId(Long userId);

  Optional<ProPassPurchase> findByOrganizationId(Long organizationId);
}
